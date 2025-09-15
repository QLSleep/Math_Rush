package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum SessionStatus {
  ACTIVE("active"),
  COMPLETED("completed"),
  ABANDONED("abandoned");

  @EnumValue
  private final String value;

  SessionStatus(String value) {
    this.value = value;
  }

  public static SessionStatus fromValue(String value) {
    for (SessionStatus status : SessionStatus.values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("No matching SessionStatus for value: " + value);
  }
}