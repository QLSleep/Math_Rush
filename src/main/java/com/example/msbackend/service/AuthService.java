package com.example.msbackend.service;

import com.example.msbackend.entity.Result;
import com.example.msbackend.vo.LoginUserVO;

public interface AuthService {

  Result<?> login(LoginUserVO user);

  Result<?> logout(String accessToken);

  Result<?> refreshAccessToken(String refreshToken);

  Result<?> generateCaptcha();

}
