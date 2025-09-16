package com.example.msbackend.interceptor;

import com.alibaba.fastjson2.JSON;
import com.example.msbackend.config.RedisConfig;
import com.example.msbackend.entity.Result;
import com.example.msbackend.enums.ResultCode;
import com.example.msbackend.utils.JwtUtils;
import com.example.msbackend.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器 - 支持双token认证机制
 * 功能：验证access token的有效性，区分token无效和过期的情况
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

  @Resource
  private JwtUtils jwtUtils;

  @Resource
  private RedisUtils redisUtils;

  @Resource
  private RedisConfig redisConfig;

  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String HEADER_NAME = "Authorization";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // 获取请求头中的Authorization
    String authorization = request.getHeader(HEADER_NAME);
    
    // 如果Authorization为空，直接返回未认证状态
    if (!StringUtils.hasText(authorization) || !authorization.startsWith(TOKEN_PREFIX)) {
      writeErrorResponse(response, ResultCode.UNAUTHORIZED.getCode(), "请先登录");
      return false;
    }
    
    // 提取token
    String accessToken = authorization.substring(TOKEN_PREFIX.length());
    
    try {
      // 检查token是否有效
      if (!jwtUtils.validateAccessToken(accessToken)) {
        // token无效，检查是否已过期
        if (jwtUtils.isTokenExpired(accessToken)) {
          // access token已过期，返回特定业务码
          writeErrorResponse(response, ResultCode.TOKEN_EXPIRED.getCode(), "access token已过期，请使用refresh token刷新");
        } else {
          // token无效（非过期原因）
          writeErrorResponse(response, ResultCode.TOKEN_INVALID.getCode(), "无效的access token");
        }
        return false;
      }
      
      // 从token中获取用户ID
      Long userId = jwtUtils.getUserIdFromAccessToken(accessToken);
      if (userId == null) {
        writeErrorResponse(response, ResultCode.TOKEN_INVALID.getCode(), "无效的access token");
        return false;
      }
      
      // 验证token是否与Redis中存储的一致（防止令牌被提前失效）
      String redisTokenKey = redisConfig.getRedisKeyPrefix() + "user:login:" + userId;
      String redisToken = (String) redisUtils.get(redisTokenKey);
      
      if (!StringUtils.hasText(redisToken) || !redisToken.equals(accessToken)) {
        writeErrorResponse(response, ResultCode.TOKEN_INVALID.getCode(), "access token已失效，请重新登录");
        return false;
      }
      
      // token验证通过，将用户信息存入请求属性中，方便后续使用
      request.setAttribute("userId", userId);
      request.setAttribute("roles", jwtUtils.getRolesFromAccessToken(accessToken));
      
      return true;
      
    } catch (Exception e) {
      // 捕获所有其他异常，返回token无效
      writeErrorResponse(response, ResultCode.TOKEN_INVALID.getCode(), "无效的access token");
      return false;
    }
  }
  
  /**
   * 向HTTP响应中写入错误信息
   * @param response HTTP响应对象
   * @param code 状态码
   * @param message 错误信息
   * @throws Exception 写入异常
   */
  private void writeErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
    // 创建Result对象
    Result<?> result = Result.error(code, message);
    
    // 设置响应头
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    
    // 使用FastJson序列化Result对象并写入响应
    String json = JSON.toJSONString(result);
    response.getWriter().write(json);
    response.getWriter().flush();
  }
}
