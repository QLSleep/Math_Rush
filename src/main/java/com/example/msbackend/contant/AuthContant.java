package com.example.msbackend.contant;

public final class AuthContant {
  // -------------------------- 常量配置 --------------------------
  public static final int LOGIN_FAIL_THRESHOLD = 5; // 登录失败阈值（超过需验证码）
  public static final long LOGIN_FAIL_EXPIRE = 3600; // 失败次数过期时间（1小时，秒）
  public static final String CAPTCHA_KEY_PREFIX = "captcha:"; // 验证码Redis键前缀（通用）
  public static final String USER_LOGIN_KEY_PREFIX = "user:login:"; // 用户登录状态键前缀
  public static final String USER_LOGIN_INFO_KEY_PREFIX = "user:login:info:"; //用户登录信息前缀
  public static final String USER_REFRESH_KEY_PREFIX = "user:refresh:";
}
