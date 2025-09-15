package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ParticipantStatus {
  JOINED("joined"),
  LEFT("left"),
  COMPLETED("completed");

  @EnumValue
  private final String value;

  ParticipantStatus(String value) {
    this.value = value;
  }

  public static ParticipantStatus fromValue(String value) {
    for (ParticipantStatus status : ParticipantStatus.values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("No matching ParticipantStatus for value: " + value);
  }
}