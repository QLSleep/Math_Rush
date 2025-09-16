package com.example.msbackend.service.impl;

import com.example.msbackend.config.RedisConfig;
import com.example.msbackend.contant.AuthContant;
import com.example.msbackend.dto.InsertUserDTO;
import com.example.msbackend.entity.Result;
import com.example.msbackend.enums.ResultCode;
import com.example.msbackend.enums.RoleNames;
import com.example.msbackend.mapper.RoleMapper;
import com.example.msbackend.mapper.UserMapper;
import com.example.msbackend.mapper.UserRoleMapper;
import com.example.msbackend.service.UserService;
import com.example.msbackend.utils.BCryptUtils;
import com.example.msbackend.utils.RedisUtils;
import com.example.msbackend.vo.RegisterVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
  
  @Resource
  private UserMapper userMapper;
  
  @Resource
  private UserRoleMapper userRoleMapper;
  
  @Resource
  private RoleMapper roleMapper;
  
  @Resource
  private RedisUtils redisUtils;
  
  @Resource
  private RedisConfig redisConfig;

  /**
   * 用户注册服务
   * <p>处理用户注册请求，包括参数验证、验证码校验、用户名/邮箱重复性检查、密码加密、用户创建以及角色关联等操作</p>
   * 
   * @param registerVO 注册请求参数对象，包含用户注册所需的各种信息
   * @return 注册结果对象，包含成功或失败信息
   *         <ul>
   *           <li>成功：Result.success()</li>
   *           <li>失败：Result.error(code, message)，具体错误码和信息根据失败原因而定</li>
   *         </ul>
   */
  @Override
  public Result<?> register(RegisterVO registerVO) {
    String email = registerVO.getEmail();
    String username = registerVO.getUsername();
    String password = registerVO.getPassword();
    String captcha = registerVO.getCaptcha();
    String captchaId = registerVO.getCaptchaId();
    
    // 1. 验证参数是否为空
    if (!StringUtils.hasText(email) || !StringUtils.hasText(username) || 
        !StringUtils.hasText(password) || !StringUtils.hasText(captcha) || !StringUtils.hasText(captchaId)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "注册信息不能为空");
    }
    
    // 2. 验证验证码是否正确
    // 使用captchaId作为Redis键的一部分，与AuthServiceImpl中的实现保持一致
    String captchaKey = redisConfig.getRedisKeyPrefix() + AuthContant.CAPTCHA_KEY_PREFIX + captchaId;
    String redisCaptcha = (String) redisUtils.get(captchaKey);
    if (redisCaptcha == null || !redisCaptcha.equalsIgnoreCase(captcha)) {
      return Result.error(ResultCode.USER_CAPTCHA_ERROR.getCode(), "验证码错误或已过期");
    }
    
    // 3. 检查用户名或邮箱是否已存在
    boolean exists = userMapper.checkUsernameOrEmailExists(username, email);
    if (exists) {
      return Result.error(ResultCode.USER_ALREADY_EXISTS.getCode(), "用户名或邮箱已被注册");
    }
    
    // 4. 对密码进行加密
    String encryptedPassword = BCryptUtils.encode(password);
    
    // 5. 创建用户
    InsertUserDTO insertUserDTO = new InsertUserDTO();
    insertUserDTO.setUsername(username);
    insertUserDTO.setPassword(encryptedPassword);
    insertUserDTO.setEmail(email);
    
    boolean insertResult = userMapper.insertUser(insertUserDTO);
    if (insertResult) {
      // 获取插入的用户ID（通过useGeneratedKeys自动设置到insertUserDTO中）
      Long userId = insertUserDTO.getId();
      
      // 6. 单独处理用户角色关联
      // 根据角色名获取角色ID（使用RoleMapper）
      Integer roleId = roleMapper.getRoleIdByRoleName(RoleNames.NORMAL.getRoleName());
      
      if (roleId == null) {
        // 角色不存在
        return Result.error(ResultCode.FAIL.getCode(), "角色不存在，请联系管理员");
      }
      
      // 使用UserRoleMapper插入用户角色关联
      boolean roleInsertResult = userRoleMapper.insertUserRole(userId, roleId);
      
      if (roleInsertResult) {
        // 注册成功后删除验证码
        redisUtils.delKey(captchaKey);
        return Result.success();
      } else {
        // 角色关联失败，可能需要回滚用户插入操作
        return Result.error(ResultCode.FAIL.getCode(), "角色设置失败，请稍后重试");
      }
    } else {
      return Result.error(ResultCode.FAIL.getCode(), "注册失败，请稍后重试");
    }
  }

  @Override
  public Result<?> changeAccount() {
    return null;
  }
}
