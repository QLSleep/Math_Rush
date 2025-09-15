package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum SessionType {
  SINGLE("single"),
  MATCH("match");

  @EnumValue
  private final String value;

  SessionType(String value) {
    this.value = value;
  }

  public static SessionType fromValue(String value) {
    for (SessionType type : SessionType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No matching SessionType for value: " + value);
  }
}