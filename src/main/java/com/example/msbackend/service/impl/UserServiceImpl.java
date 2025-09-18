package com.example.msbackend.service.impl;

import com.example.msbackend.config.RedisConfig;
import com.example.msbackend.contant.AuthContant;
import com.example.msbackend.dto.CancelUserDTO;
import com.example.msbackend.dto.InsertUserDTO;
import com.example.msbackend.dto.ModifyUserInfoDTO;
import com.example.msbackend.dto.ChangePwdDTO;
import com.example.msbackend.entity.Result;
import com.example.msbackend.enums.ResultCode;
import com.example.msbackend.enums.RoleNames;
import com.example.msbackend.mapper.RoleMapper;
import com.example.msbackend.mapper.UserMapper;
import com.example.msbackend.mapper.UserRoleMapper;
import com.example.msbackend.service.UserService;
import com.example.msbackend.utils.BCryptUtils;
import com.example.msbackend.utils.RedisUtils;
import com.example.msbackend.vo.CancelUserVO;
import com.example.msbackend.vo.ChangePwdVO;
import com.example.msbackend.vo.ModifyUserInfoVO;
import com.example.msbackend.vo.RegisterVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(rollbackFor = Exception.class)
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
  public Result<?> cancelUser(CancelUserVO cancelUserVO) {
    String username = cancelUserVO.getUsername();
    String password = cancelUserVO.getPassword();
    String captcha = cancelUserVO.getCaptcha();
    String captchaId = cancelUserVO.getCaptchaId();

    // 验证参数是否为空
    if (!StringUtils.hasText(username) || !StringUtils.hasText(password) ||
        !StringUtils.hasText(captcha) || !StringUtils.hasText(captchaId)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "信息不能为空");
    }

    // 校验验证码
    String captchaKey = redisConfig.getRedisKeyPrefix() + AuthContant.CAPTCHA_KEY_PREFIX + captchaId;
    String redisCaptcha = (String) redisUtils.get(captchaKey);
    if (redisCaptcha == null || !redisCaptcha.equalsIgnoreCase(captcha)) {
      return Result.error(ResultCode.USER_CAPTCHA_ERROR.getCode(), "验证码错误或已过期");
    }

    // 查询用户信息
    CancelUserDTO cancelUserDTO = userMapper.getCancelUserDTO(username);
    if (cancelUserDTO == null) {
      return Result.error(ResultCode.USER_NOT_EXIST.getCode(), "用户不存在");
    }

    // 校验密码
    boolean passwordMatch = BCryptUtils.matches(password, cancelUserDTO.getPassword());
    if (!passwordMatch) {
      return Result.error(ResultCode.USER_PASSWORD_ERROR.getCode(), "密码错误");
    }

    // 先删除用户角色关联记录
    boolean deleteRolesResult = userRoleMapper.deleteUserRolesByUserId(cancelUserDTO.getUserId());
    // 再删除用户记录
    boolean cancelResult = userMapper.deleteUserById(cancelUserDTO.getUserId());
    if (deleteRolesResult && cancelResult) {
      redisUtils.delKey(captchaKey);

      // 用户注销成功后，强制用户重新登录（清除用户的登录状态缓存）
      Long userId = cancelUserDTO.getUserId();
      String userLoginKey = redisConfig.getRedisKeyPrefix() + "user:login:" + userId;
      String userLoginInfoKey = redisConfig.getRedisKeyPrefix() + "user:login:info:" + userId;
      String userRefreshMapKey = redisConfig.getRedisKeyPrefix() + "user:refresh:user:" + userId;

      // 获取并删除refreshToken
      String oldRefreshJti = (String) redisUtils.get(userRefreshMapKey);
      if (StringUtils.hasText(oldRefreshJti)) {
        String oldRefreshKey = redisConfig.getRedisKeyPrefix() + "user:refresh:" + oldRefreshJti;
        redisUtils.delKey(oldRefreshKey);
      }

      // 删除所有相关的登录状态信息
      redisUtils.delKey(userLoginKey);
      redisUtils.delKey(userLoginInfoKey);
      redisUtils.delKey(userRefreshMapKey);

      return Result.success("用户注销成功");
    } else {
      return Result.error(ResultCode.FAIL.getCode(), "用户注销失败，请稍后重试");
    }
  }

  /**
   * 修改用户密码服务
   * <p>处理用户修改密码请求，采用双密码（旧密码和两遍新密码）+验证码服务进行安全保障</p>
   * 
   * @param changePwdVO 修改密码请求参数对象，包含用户名、当前密码、新密码、确认密码、验证码和验证码ID
   * @return 修改结果对象，包含成功或失败信息
   *         <ul>
   *           <li>成功：Result.success()</li>
   *           <li>失败：Result.error(code, message)，具体错误码和信息根据失败原因而定</li>
   *         </ul>
   */
  @Override
  public Result<?> changePassword(ChangePwdVO changePwdVO) {
    String username = changePwdVO.getUsername();
    String currentPwd = changePwdVO.getCurrentPwd();
    String newPwd = changePwdVO.getNewPwd();
    String confirmPwd = changePwdVO.getConfirmPwd();
    String captcha = changePwdVO.getCaptcha();
    String captchaId = changePwdVO.getCaptchaId();
    
    // 1. 验证参数是否为空
    if (!StringUtils.hasText(username) || !StringUtils.hasText(currentPwd) || 
        !StringUtils.hasText(newPwd) || !StringUtils.hasText(confirmPwd) || 
        !StringUtils.hasText(captcha) || !StringUtils.hasText(captchaId)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "修改密码信息不能为空");
    }
    
    // 2. 验证两次新密码是否一致
    if (!newPwd.equals(confirmPwd)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "两次输入的新密码不一致");
    }
    
    // 3. 验证验证码是否正确
    String captchaKey = redisConfig.getRedisKeyPrefix() + AuthContant.CAPTCHA_KEY_PREFIX + captchaId;
    String redisCaptcha = (String) redisUtils.get(captchaKey);
    if (redisCaptcha == null || !redisCaptcha.equalsIgnoreCase(captcha)) {
      return Result.error(ResultCode.USER_CAPTCHA_ERROR.getCode(), "验证码错误或已过期");
    }
    
    // 4. 使用用户名查询用户信息（包含加密密码）
    ChangePwdDTO userPwdDTO = userMapper.getChangePwdDTO(username);
    if (userPwdDTO == null) {
      return Result.error(ResultCode.USER_NOT_EXIST.getCode(), "用户不存在");
    }
    
    // 5. 验证旧密码是否正确
    boolean passwordMatch = BCryptUtils.matches(currentPwd, userPwdDTO.getPassword());
    if (!passwordMatch) {
      return Result.error(ResultCode.USER_PASSWORD_ERROR.getCode(), "当前密码错误");
    }
    
    // 6. 对新密码进行加密
    String encryptedNewPassword = BCryptUtils.encode(newPwd);
    
    // 7. 比对从数据库查询出来的旧密码和加密后的新密码，确保新密码与旧密码不同
    passwordMatch = BCryptUtils.matches(newPwd, userPwdDTO.getPassword());
    if (passwordMatch) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "新密码不能与旧密码相同");
    }
    
    // 8. 直接使用旧的DTO设置新密码，不创建新的DTO对象
    userPwdDTO.setPassword(encryptedNewPassword);
    
    boolean updateResult = userMapper.changePwd(userPwdDTO);
    if (updateResult) {
      // 9. 密码修改成功后删除验证码
      redisUtils.delKey(captchaKey);
      
      // 10. 密码修改成功后，强制用户重新登录（清除用户的登录状态缓存）
      Long userId = userPwdDTO.getUserId();
      String userLoginKey = redisConfig.getRedisKeyPrefix() + "user:login:" + userId;
      String userLoginInfoKey = redisConfig.getRedisKeyPrefix() + "user:login:info:" + userId;
      String userRefreshMapKey = redisConfig.getRedisKeyPrefix() + "user:refresh:user:" + userId;
      
      // 获取并删除refreshToken
      String oldRefreshJti = (String) redisUtils.get(userRefreshMapKey);
      if (StringUtils.hasText(oldRefreshJti)) {
        String oldRefreshKey = redisConfig.getRedisKeyPrefix() + "user:refresh:" + oldRefreshJti;
        redisUtils.delKey(oldRefreshKey);
      }
      
      // 删除所有相关的登录状态信息
      redisUtils.delKey(userLoginKey);
      redisUtils.delKey(userLoginInfoKey);
      redisUtils.delKey(userRefreshMapKey);
      
      return Result.success("密码修改成功，请重新登录");
    } else {
      return Result.error(ResultCode.FAIL.getCode(), "密码修改失败，请稍后重试");
    }
  }

  /**
   * 修改用户账户信息
   * <p>此方法用于更新用户的用户名和邮箱信息，支持单独修改用户名、单独修改邮箱或同时修改两者。
   * 系统会执行一系列验证来确保数据的有效性和唯一性，然后通过用户ID进行精准更新。</p>
   * 
   * <h3>处理流程：</h3>
   * <ol>
   *   <li>验证请求参数是否为空</li>
   *   <li>获取当前用户名、邮箱以及新的用户名、邮箱</li>
   *   <li>验证当前用户名和邮箱是否为空</li>
   *   <li>处理新用户名和新邮箱（去除空格和换行符）</li>
   *   <li>判断是否有需要更新的内容</li>
   *   <li>如果没有需要更新的内容，直接返回成功信息</li>
   *   <li>检查用户是否存在并获取用户ID（通过用户名和邮箱）</li>
   *   <li>检查新用户名是否已被其他用户使用（如果有修改用户名）</li>
   *   <li>检查新邮箱是否已被其他用户使用（如果有修改邮箱）</li>
   *   <li>封装更新信息到DTO对象中，执行数据库更新操作</li>
   *   <li>根据更新结果返回成功或失败信息</li>
   * </ol>
   * 
   * @param modifyUserInfoVO 修改用户信息的请求对象
   *                         <ul>
   *                           <li>username：当前用户名（必填）</li>
   *                           <li>email：当前邮箱（必填）</li>
   *                           <li>newUsername：新用户名（可选，可为空或不填）</li>
   *                           <li>newEmail：新邮箱（可选，可为空或不填）</li>
   *                         </ul>
   * @return 修改结果对象，包含成功或失败信息
   *         <ul>
   *           <li>成功：code=0，message包含成功信息</li>
   *           <li>失败：code>0，message包含具体错误信息</li>
   *         </ul>
   * @throws IllegalArgumentException 当请求参数不合法时抛出
   */
  @Override
  public Result<?> changeAccount(ModifyUserInfoVO modifyUserInfoVO) {
    // 1. 验证请求参数是否为空
    if (modifyUserInfoVO == null) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "请求参数不能为空");
    }
    
    // 2. 获取参数
    String currentUsername = modifyUserInfoVO.getUsername();
    String currentEmail = modifyUserInfoVO.getEmail();
    String newUsername = modifyUserInfoVO.getNewUsername();
    String newEmail = modifyUserInfoVO.getNewEmail();
    
    // 3. 验证当前用户名和邮箱是否为空
    if (!StringUtils.hasText(currentUsername) || !StringUtils.hasText(currentEmail)) {
      return Result.error(ResultCode.PARAM_ERROR.getCode(), "当前用户名和邮箱不能为空");
    }
    
    // 4. 去除新用户名和新邮箱的空格和换行符
    String processedNewUsername = newUsername != null ? newUsername.trim() : null;
    String processedNewEmail = newEmail != null ? newEmail.trim() : null;
    
    // 5. 判断是否有需要更新的内容
    boolean needUpdate = false;
    boolean hasUsernameChange = false;
    boolean hasEmailChange = false;
    
    // 检查新用户名是否与当前用户名不同且不为空
    if (processedNewUsername != null && !processedNewUsername.isEmpty() && !processedNewUsername.equals(currentUsername)) {
      hasUsernameChange = true;
      needUpdate = true;
    }
    
    // 检查新邮箱是否与当前邮箱不同且不为空
    if (processedNewEmail != null && !processedNewEmail.isEmpty() && !processedNewEmail.equals(currentEmail)) {
      hasEmailChange = true;
      needUpdate = true;
    }
    
    // 6. 如果没有需要更新的内容，直接返回成功
    if (!needUpdate) {
      return Result.success("用户信息无需修改");
    }
    
    // 7. 检查用户是否存在并获取用户ID
    Long userId = userMapper.getUserByUsernameAndEmail(currentUsername, currentEmail);
    if (userId == null) {
      return Result.error(ResultCode.USER_NOT_EXIST.getCode(), "用户不存在");
    }

    // 8. 检查新用户名是否已被其他用户使用
    if (hasUsernameChange) {
      boolean usernameExists = userMapper.checkUsernameExistsExcludeCurrent(currentUsername, currentEmail, processedNewUsername);
      if (usernameExists) {
        return Result.error(ResultCode.USER_ALREADY_EXISTS.getCode(), "新用户名已被使用");
      }
    }

    // 9. 检查新邮箱是否已被其他用户使用
    if (hasEmailChange) {
      boolean emailExists = userMapper.checkEmailExistsExcludeCurrent(currentUsername, currentEmail, processedNewEmail);
      if (emailExists) {
        return Result.error(ResultCode.USER_ALREADY_EXISTS.getCode(), "新邮箱已被使用");
      }
    }

    // 10. 封装更新信息到DTO中，执行更新操作
    ModifyUserInfoDTO modifyUserInfoDTO = new ModifyUserInfoDTO();
    modifyUserInfoDTO.setId(userId);
    modifyUserInfoDTO.setUsername(currentUsername);
    modifyUserInfoDTO.setEmail(currentEmail);
    modifyUserInfoDTO.setNewUsername(hasUsernameChange ? processedNewUsername : currentUsername);
    modifyUserInfoDTO.setNewEmail(hasEmailChange ? processedNewEmail : currentEmail);
    
    boolean updateResult = userMapper.updateUserInfo(modifyUserInfoDTO);
    if (updateResult) {
      return Result.success("用户信息修改成功");
    } else {
      return Result.error(ResultCode.FAIL.getCode(), "用户信息修改失败，请稍后重试");
    }
  }
}
