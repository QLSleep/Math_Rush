package com.example.msbackend.service;

import com.example.msbackend.entity.Result;
import com.example.msbackend.vo.LoginUserVO;

public interface AuthService {

  public Result<?> login(LoginUserVO user);

  public Result<?> logout(String accesstoken);

  public Result<?> refreshAccessToken(String refreshToken);

}
