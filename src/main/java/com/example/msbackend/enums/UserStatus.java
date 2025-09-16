package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStatus {
  NORMAL((byte) 1),
  BANNED((byte) 2);

  @EnumValue
  private final Byte code;

  UserStatus(Byte code) {
    this.code = code;
  }

  public UserStatus fromCode(byte code) {
    for (UserStatus userStatus : UserStatus.values()) {
      if (userStatus.code.equals(code)) {
        return userStatus;
      }
    }
    throw new IllegalArgumentException("No matching UserStatus for code: " + code);
  }
}
