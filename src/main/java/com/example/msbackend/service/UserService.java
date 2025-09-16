package com.example.msbackend.service;

import com.example.msbackend.entity.Result;
import com.example.msbackend.vo.ModifyUserInfoVO;
import com.example.msbackend.vo.RegisterVO;

public interface UserService {

  Result<?> register(RegisterVO registerVO);

//  public Result<?> forgetPassword();

//  public Result<?> cancelAccount();

//  public Result<?> changePassword();

  Result<?> changeAccount(ModifyUserInfoVO modifyUserInfoVO);
}
