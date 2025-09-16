package com.example.msbackend.controller;

import com.example.msbackend.entity.Result;
import com.example.msbackend.service.AuthService;
import com.example.msbackend.vo.LoginUserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户登录、登出、刷新令牌和生成验证码等认证相关请求
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Resource
  private AuthService authService;

  /**
   * 用户登录接口
   * @param userVO 登录用户信息
   * @return 登录结果
   */
  @PostMapping("/login")
  public Result<?> login(@RequestBody LoginUserVO userVO) {
    return authService.login(userVO);
  }

  /**
   * 用户登出接口
   * @param accessToken 访问令牌
   * @return 登出结果
   */
  @PostMapping("/logout")
  public Result<?> logout(@RequestParam String accessToken) {
    return authService.logout(accessToken);
  }

  /**
   * 刷新访问令牌接口
   * @param refreshToken 刷新令牌
   * @return 刷新结果
   */
  @PostMapping("/refresh-token")
  public Result<?> refreshAccessToken(@RequestParam String refreshToken) {
    return authService.refreshAccessToken(refreshToken);
  }

  /**
   * 生成验证码接口
   * 用于登录失败次数过多或注册时的验证码验证
   * @return 验证码图片的Base64编码和验证码ID
   */
  @GetMapping("/captcha")
  public Result<?> generateCaptcha() {
    return authService.generateCaptcha();
  }
}