package com.example.msbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
  private String secret;
  private long accessTokenExpire;  //单位秒

  public long getAccessTokenExpireMillis(){
    return accessTokenExpire*1000;
  }
}
