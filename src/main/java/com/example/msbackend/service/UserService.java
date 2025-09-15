package com.example.msbackend.service;

public interface UserService {

  public boolean register();

  public boolean forgetPassword();

  public boolean cancelAccount();

  public boolean changePassword();

  public boolean changeAccount();
}
