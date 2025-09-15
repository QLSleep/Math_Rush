package com.example.msbackend.utils;

import com.example.msbackend.config.JwtConfig;
import com.example.msbackend.vo.JWTUserVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {
  @Resource
  private JwtConfig jwtConfig;
  private Key signingKey;

  @PostConstruct
  public void init() {
    this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 生成Access Token
   */
  public String generateAccessToken(JWTUserVO user) {
    String jti = "access_" + UUID.randomUUID();
    return Jwts.builder()
        .setSubject(String.valueOf(user.getUserId()))
        .setIssuer("math_rush0.1")
        .setAudience("api")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpireMillis()))
        .setId(jti)
        .claim("token_type", "access")
        .claim("roles", user.getRoles())
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 解析Token
   */
  public Claims parseAccessToken(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (JwtException e) {
      // 可以记录日志或抛出自定义异常
      throw new RuntimeException("Invalid token: " + e.getMessage(), e);
    }
  }

  /**
   * 验证Token是否有效
   */
  public boolean validateAccessToken(String token) {
    try {
      Claims claims = parseAccessToken(token);
      return claims != null && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 从Token中获取用户ID（仅适用于Access Token）
   */
  public Long getUserIdFromAccessToken(String token) {
    try {
      Claims claims = parseAccessToken(token);
      return Long.parseLong(claims.getSubject());
    } catch (Exception e) {
      return null;
    }
  }

  /**
   *
   * 从Token中获取角色列表
   */
  public List<String> getRolesFromAccessToken(String token) {
    try {
      Claims claims = parseAccessToken(token);
      return claims.get("roles", List.class);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  /**
   * 检查Token是否已过期
   */
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = parseAccessToken(token);
      return claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }

  /**
   * 获取Token的剩余有效期（毫秒）
   */
  public long getTokenRemainingTime(String token) {
    try {
      Claims claims = parseAccessToken(token);
      return claims.getExpiration().getTime() - new Date().getTime();
    } catch (Exception e) {
      return -1;
    }
  }
}