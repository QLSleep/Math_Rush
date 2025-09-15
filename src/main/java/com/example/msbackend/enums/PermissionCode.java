package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PermissionCode {
  ADMIN("admin"),
  NORMAL("normal");

  @EnumValue
  private final String code;

  PermissionCode(String code) {
    this.code = code;
  }

  public PermissionCode fromCode(String code) {
    for (PermissionCode permissionCode : PermissionCode.values()) {
      if (permissionCode.getCode().equals(code)) {
        return permissionCode;
      }
    }
    throw new IllegalArgumentException("No matching PermissionCode for code: " + code);
  }
}
