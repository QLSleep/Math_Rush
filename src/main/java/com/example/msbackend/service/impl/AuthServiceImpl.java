package com.example.msbackend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.msbackend.config.RedisConfig;
import com.example.msbackend.dto.UserInfoDTO;
import com.example.msbackend.entity.Result;
import com.example.msbackend.entity.User;
import com.example.msbackend.enums.ResultCode;
import com.example.msbackend.mapper.UserMapper;
import com.example.msbackend.service.AuthService;
import com.example.msbackend.utils.BCryptUtils;
import com.example.msbackend.utils.JwtUtils;
import com.example.msbackend.utils.RedisUtil;
import com.example.msbackend.vo.JWTUserVO;
import com.example.msbackend.vo.LoginUserVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.msbackend.contant.AuthContant.*;

@Service
public class AuthServiceImpl implements AuthService {

  @Resource
  private RedisConfig redisConfig;

  @Resource
  private RedisUtil redisUtil;

  @Resource
  private JwtUtils jwtUtils;

  @Resource
  private UserMapper userMapper;

  /**
   * 用户登录服务方法
   * <p>功能：处理用户登录请求，验证用户名密码，实现账号挤兑，生成并存储相关token</p>
   * <p>账号挤兑机制：当同一账号在新设备登录时，会自动挤兑掉旧设备的登录状态</p>
   * <p>生成的Token说明：</p>
   * <ul>
   *   <li>accessToken：JWT访问令牌，用于API访问认证</li>
   *   <li>refreshToken：刷新令牌，用于刷新accessToken，有效期7天</li>
   * </ul>
   * <p>Redis存储的键格式：</p>
   * <ul>
   *   <li>登录状态键：[redis前缀]user:login:[用户ID]，存储accessToken</li>
   *   <li>用户登录信息：user:login:info:[用户ID]，存储用户详细信息</li>
   *   <li>refreshToken：[redis前缀]user:refresh:[refresh_jti]，存储refreshToken</li>
   *   <li>用户-refreshToken映射：[redis前缀]user:refresh:user:[用户ID]，存储jti，用于挤兑操作</li>
   * </ul>
   * 
   * @param userVO 登录用户信息，包含用户名、密码和验证码
   * @return 登录结果，成功时包含accessToken、refreshToken和用户信息
   */
  @Override
  public Result<?> login(LoginUserVO userVO) {
    String username = userVO.getUsername();
    String password = userVO.getPassword();
    String captcha = userVO.getCaptcha();

    if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "用户名或密码不能为空");
    }

    String failCountKey = redisConfig.getRedisKeyPrefix() + "login:fail:" + username;
    Long failCount = (Long) redisUtil.get(failCountKey);
    if (failCount == null) {
      failCount = 0L; // 首次失败时初始化为0
    }
    boolean needCaptcha = failCount >= LOGIN_FAIL_THRESHOLD;

    //验证码验证
    if (needCaptcha) {
      if(!StringUtils.hasText(captcha)) {
        return Result.error(ResultCode.USER_LOGIN_FAIL_TOO_MANY);
      }
      String captchaKey = redisConfig.getRedisKeyPrefix() + CAPTCHA_KEY_PREFIX + username;
      String storedCaptcha = (String) redisUtil.get(captchaKey);
      if (!StringUtils.hasText(storedCaptcha) || !captcha.equalsIgnoreCase(storedCaptcha)) {
        return Result.error(ResultCode.USER_CAPTCHA_ERROR);
      }
    }

    //查询数据库
    User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    if (user == null) {
      redisUtil.set(failCountKey,failCount+1 ,LOGIN_FAIL_EXPIRE);
      return Result.error(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }
    //校验密码
    if (!BCryptUtils.matches(userVO.getPassword(), user.getPassword())){
      redisUtil.set(failCountKey,failCount+1 ,LOGIN_FAIL_EXPIRE);
      return Result.error(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }

    //登录成功，清理无效状态+生成token+存储登录状态
    redisUtil.delKey(failCountKey);
    redisUtil.delKey(redisConfig.getRedisKeyPrefix() + CAPTCHA_KEY_PREFIX + username);//清理验证码

    //禁止重复登陆(新设备挤兑旧设备)
    String userLoginKey = redisConfig.getRedisKeyPrefix() + USER_LOGIN_KEY_PREFIX + user.getId();
    String existingToken = (String) redisUtil.get(userLoginKey);
    //如果账号已有设备登录，执行挤兑操作
    if (StringUtils.hasText(existingToken)) {
//      return Result.error(ResultCode.USER_ALREADY_LOGGED_IN);
      redisUtil.delKey(userLoginKey);
      
      // 删除旧的refreshToken
      // 首先获取用户对应的refreshToken键
      String userRefreshKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + "user:" + user.getId();
      String oldRefreshJti = (String) redisUtil.get(userRefreshKey);
      
      // 如果存在旧的refreshToken，删除它
      if (StringUtils.hasText(oldRefreshJti)) {
        String oldRefreshKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + oldRefreshJti;
        redisUtil.delKey(oldRefreshKey);
        // 同时删除用户到refreshToken的映射
        redisUtil.delKey(userRefreshKey);
      }
    }

    //生成jwt acctoken
    UserInfoDTO userInfoDTO = userMapper.getUserInfo(username);
    JWTUserVO jwtUserVO = new JWTUserVO();
    jwtUserVO.setUserId(userInfoDTO.getId());
    jwtUserVO.setRoles(userInfoDTO.getRoles());
    String accessToken = jwtUtils.generateAccessToken(jwtUserVO);

    //存储登录相关信息至redis
    //存入已登录键
    redisUtil.set(userLoginKey, accessToken);
    //存入用户登录信息
    Map<String, Object> map = new HashMap<>();
    map.put("id", userInfoDTO.getId());
    map.put("username", userInfoDTO.getUsername());
    map.put("email", userInfoDTO.getEmail());
    map.put("createdAt", userInfoDTO.getCreatedAt().toString());
    map.put("updatedAt", userInfoDTO.getUpdatedAt().toString());
    map.put("roles", JSON.toJSONString(userInfoDTO.getRoles())); // List 序列化为 JSON 字符串
    // 确保每个用户的登录信息存储在独立的键中
    String userLoginInfoKey = USER_LOGIN_INFO_KEY_PREFIX + userInfoDTO.getId();
    redisUtil.hset(userLoginInfoKey, map);
    //存储refreshtoken
    String jti = "refresh_" + UUID.randomUUID();
    String refreshKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + jti;
    redisUtil.set(refreshKey, user.getId(), 60*60*24*7); //时长为七天
    
    // 存储用户ID到refreshToken的映射，用于后续挤兑操作
    String userRefreshKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + "user:" + user.getId();
    redisUtil.set(userRefreshKey, jti, 60*60*24*7); // 与refreshToken相同的过期时间

    //构造响应信息
    Map<String, Object> loginResult = new HashMap<>();
    loginResult.put("accessToken", accessToken);
    loginResult.put("refreshKey", refreshKey);
    loginResult.put("userInfo", userInfoDTO);

    return Result.success("登录成功", loginResult);
  }

  /**
   * 用户登出服务方法
   * <p>功能：处理用户登出请求，清除用户的登录状态和相关token</p>
   * <p>登出流程：</p>
   * <ul>
   *   <li>1. 解析accessToken获取用户ID</li>
   *   <li>2. 删除用户的登录状态缓存</li>
   *   <li>3. 删除用户的详细信息缓存</li>
   *   <li>4. 删除用户的refreshToken及其映射关系</li>
   * </ul>
   * <p>Redis删除的键：</p>
   * <ul>
   *   <li>登录状态键：[redis前缀]user:login:[用户ID]</li>
   *   <li>用户登录信息：[redis前缀]user:login:info:[用户ID]</li>
   *   <li>refreshToken：[redis前缀]user:refresh:[refresh_jti]</li>
   *   <li>用户-refreshToken映射：[redis前缀]user:refresh:user:[用户ID]</li>
   * </ul>
   * 
   * @param accesstoken 用户的访问令牌
   * @return 登出结果，成功时返回操作结果信息
   */
  @Override
  public Result<?> logout(String accesstoken) {
    // 解析accessToken获取用户ID
    Claims claims = jwtUtils.parseAccessToken(accesstoken);
    long userId = Long.parseLong(claims.getSubject());
    
    // 构建需要删除的Redis键
    String userLoginKey = redisConfig.getRedisKeyPrefix() + USER_LOGIN_KEY_PREFIX + userId;
    String userLoginInfoKey = redisConfig.getRedisKeyPrefix() + USER_LOGIN_INFO_KEY_PREFIX + userId;
    String userRefreshMapKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + "user:" + userId;
    
    // 获取并删除refreshToken
    String oldRefreshJti = (String) redisUtil.get(userRefreshMapKey);
    if (StringUtils.hasText(oldRefreshJti)) {
      String oldRefreshKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + oldRefreshJti;
      redisUtil.delKey(oldRefreshKey);
    }
    
    // 删除所有相关的登录状态信息
    redisUtil.delKey(userLoginKey);
    redisUtil.delKey(userLoginInfoKey);
    redisUtil.delKey(userRefreshMapKey);
    
    return Result.success("登出成功");
  }

  /**
   * 刷新访问令牌
   * <p>功能：使用有效的refreshToken获取新的accessToken，并在适当时候更新refreshToken</p>
   * <p>刷新流程：</p>
   * <ul>
   *   <li>1. 验证refreshToken是否有效且未过期</li>
   *   <li>2. 解析refreshToken获取用户信息</li>
   *   <li>3. 检查refreshToken剩余有效期，若低于30%则更新其过期时间（最长30天）</li>
   *   <li>4. 生成新的accessToken并更新Redis缓存</li>
   *   <li>5. 返回新的accessToken和可能更新的refreshToken</li>
   * </ul>
   * 
   * @param refreshToken 刷新令牌
   * @return 刷新结果，成功时包含新的accessToken和更新后的refreshToken（如有更新）
   */
  @Override
  public Result<?> refreshAccessToken(String refreshToken) {
    // 参数校验
    if (!StringUtils.hasText(refreshToken)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "refreshToken不能为空");
    }
    
    // 获取refreshToken对应的用户ID
    Object userIdObj = redisUtil.get(refreshToken);
    if (userIdObj == null) {
      return Result.error(ResultCode.TOKEN_INVALID.getCode(), "无效的refreshToken");
    }
    
    Long userId = Long.valueOf(userIdObj.toString());
    
    // 先从Redis中获取用户信息
    String userLoginInfoKey = redisConfig.getRedisKeyPrefix() + USER_LOGIN_INFO_KEY_PREFIX + userId;
    Map<Object, Object> userInfoMap = redisUtil.hgetAll(userLoginInfoKey);
    UserInfoDTO userInfoDTO = null;
    
    if (userInfoMap != null && !userInfoMap.isEmpty()) {
      // 从Redis中构建UserInfoDTO
      userInfoDTO = new UserInfoDTO();
      userInfoDTO.setId(Long.valueOf(userInfoMap.get("id").toString()));
      userInfoDTO.setUsername(userInfoMap.get("username").toString());
      userInfoDTO.setEmail(userInfoMap.get("email") != null ? userInfoMap.get("email").toString() : null);
      // 解析角色列表
      if (userInfoMap.containsKey("roles")) {
        userInfoDTO.setRoles(JSON.parseArray(userInfoMap.get("roles").toString(), String.class));
      }
    }
    
    // 如果Redis中没有用户信息，从数据库中获取
    if (userInfoDTO == null) {
      User user = userMapper.selectById(userId);
      if (user == null) {
        return Result.error(ResultCode.USER_NOT_EXIST.getCode(), "用户不存在");
      }
      
      userInfoDTO = userMapper.getUserInfo(user.getUsername());
      if (userInfoDTO == null) {
        return Result.error(ResultCode.USER_NOT_EXIST.getCode(), "用户信息获取失败");
      }
    }
    
    // 检查refreshToken的过期时间（秒）
    long expireTime = redisUtil.getExpire(refreshToken);
    
    // 如果refreshToken已过期
    if (expireTime <= 0) {
      // 清理相关缓存
      String userRefreshMapKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + "user:" + userId;
      redisUtil.delKey(refreshToken);
      redisUtil.delKey(userRefreshMapKey);
      return Result.error(ResultCode.TOKEN_EXPIRED.getCode(), "refreshToken已过期，请重新登录");
    }
    
    // 计算总有效期和30%的阈值
    // 原始有效期是7天 = 7*24*60*60 = 604800秒
    long originalExpire = 60 * 60 * 24 * 7; // 7天
    long threshold = (long) (originalExpire * 0.3); // 30%的阈值
    
    // 新的refreshToken变量，默认为原token
    String newRefreshToken = refreshToken;
    
    // 如果剩余时间低于30%，需要更新refreshToken的过期时间
     if (expireTime < threshold) {
       // 计算新的过期时间，最长不超过30天
       // 这里取原有效期剩余时间的2倍，但不超过30天
       long newExpireTime = Math.min(60 * 60 * 24 * 30, expireTime * 2);
       
       // 更新现有refreshToken的过期时间
       redisUtil.expire(refreshToken, newExpireTime);
       
       // 同时更新用户-refreshToken映射的过期时间
       String userRefreshMapKey = redisConfig.getRedisKeyPrefix() + USER_REFRESH_KEY_PREFIX + "user:" + userId;
       redisUtil.expire(userRefreshMapKey, newExpireTime);
     }
    
    // 生成新的accessToken
    JWTUserVO jwtUserVO = new JWTUserVO();
    jwtUserVO.setUserId(userInfoDTO.getId());
    jwtUserVO.setRoles(userInfoDTO.getRoles());
    String newAccessToken = jwtUtils.generateAccessToken(jwtUserVO);
    
    // 更新Redis中的accessToken
    String userLoginKey = redisConfig.getRedisKeyPrefix() + USER_LOGIN_KEY_PREFIX + userId;
    redisUtil.set(userLoginKey, newAccessToken);
    
    // 构造响应信息
    Map<String, Object> result = new HashMap<>();
    result.put("accessToken", newAccessToken);
    result.put("refreshToken", newRefreshToken);
    
    return Result.success("令牌刷新成功", result);
  }
}
