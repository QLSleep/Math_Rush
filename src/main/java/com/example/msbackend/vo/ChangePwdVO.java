package com.example.msbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePwdVO {
  String username;
  String currentPwd;
  String newPwd;
  String confirmPwd;
  String captcha;
  String captchaId;
}
