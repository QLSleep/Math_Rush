package com.example.msbackend.enums;

public enum ResultCode {
  SUCCESS(0, "操作成功"),
  FAIL(1, "操作失败"),
  UNAUTHORIZED(401, "未认证或token已失效"),
  FORBIDDEN(403, "没有权限"),
  NOT_FOUND(404, "资源不存在"),
  SERVER_ERROR(500, "服务器内部错误"),
  USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
  USER_ALREADY_EXISTS(1002, "用户已存在"),
  PARAM_ERROR(1003, "参数错误"),
  USER_ALREADY_LOGGED_IN(1004, "用户已登录"),
  USER_CAPTCHA_ERROR(1005, "验证码错误或已过期"), // 新增
  USER_LOGIN_FAIL_TOO_MANY(1006, "登录失败次数过多，请输入验证码"), // 新增
  TOKEN_INVALID(1007, "token无效"), // 新增
  TOKEN_EXPIRED(1008, "token已过期"), // 新增
  USER_NOT_EXIST(1009, "用户不存在"); // 新增

  private final int code;
  private final String message;

  ResultCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() { return code; }
  public String getMessage() { return message; }
}