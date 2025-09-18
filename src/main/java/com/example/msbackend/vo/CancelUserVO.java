package com.example.msbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelUserVO {
  private String username;
  private String password;
  private String captcha;
  private String captchaId;
}
