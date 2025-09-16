package com.example.msbackend.service;

import com.example.msbackend.entity.Result;
import com.example.msbackend.vo.RegisterVO;

public interface UserService {

  Result<?> register(RegisterVO registerVO);

//  public Result<?> forgetPassword();

//  public Result<?> cancelAccount();

//  public Result<?> changePassword();

//  public Result<?> changeAccount();
}
