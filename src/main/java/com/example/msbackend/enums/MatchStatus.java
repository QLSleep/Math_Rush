package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum MatchStatus {
  WAITING("waiting"),
  IN_PROGRESS("in_progress"),
  COMPLETED("completed"),
  TIMEOUT("timeout");

  @EnumValue
  private final String value;

  MatchStatus(String value) {
    this.value = value;
  }

  public static MatchStatus fromValue(String value){
    for(MatchStatus matchStatus : MatchStatus.values()){
      if(matchStatus.value.equals(value)){
        return matchStatus;
      }
    }
    throw new IllegalArgumentException("No matching MatchStatus for value: " + value);
  }
}
