package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RoleNames {
  NORMAL("ROLE_NORMAL"),
  ADMIN("ROLE_ADMIN");

  @EnumValue
  private final String roleName;

  RoleNames(String roleName) {
    this.roleName = roleName;
  }

  /**
   * 根据字符串值获取枚举实例
   */
  public static RoleNames fromCode(String name) {
    for (RoleNames roleNames : RoleNames.values()) {
      if (roleNames.roleName.equals(name)) {
        return roleNames;
      }
    }
    throw new IllegalArgumentException("No matching RoleNames for code: " + name);
  }
}
