package com.example.msbackend.service;

import com.example.msbackend.entity.Result;
import com.example.msbackend.vo.CancelUserVO;
import com.example.msbackend.vo.ChangePwdVO;
import com.example.msbackend.vo.ModifyUserInfoVO;
import com.example.msbackend.vo.RegisterVO;

public interface UserService {

  Result<?> register(RegisterVO registerVO);

  Result<?> cancelUser(CancelUserVO cancelUserVO);

  Result<?> changePassword(ChangePwdVO changePwdVO);

  Result<?> changeAccount(ModifyUserInfoVO modifyUserInfoVO);
}
