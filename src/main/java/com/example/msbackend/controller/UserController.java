package com.example.msbackend.controller;

import com.example.msbackend.entity.Result;
import com.example.msbackend.service.UserService;
import com.example.msbackend.vo.ModifyUserInfoVO;
import com.example.msbackend.vo.RegisterVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 * 处理用户注册、个人信息管理等用户相关请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Resource
  private UserService userService;

  /**
   * 用户注册接口
   * @param registerVO 注册用户信息
   * @return 注册结果
   */
  @PostMapping("/register")
  public Result<?> register(@RequestBody RegisterVO registerVO) {
    return userService.register(registerVO);
  }
  
  /**
   * 更新用户账户信息接口
   * @param modifyUserInfoVO 修改用户信息
   * @return 更新结果
   */
  @PostMapping("/change-account")
  public Result<?> changeAccount(@RequestBody ModifyUserInfoVO modifyUserInfoVO) {
    return userService.changeAccount(modifyUserInfoVO);
  }
}