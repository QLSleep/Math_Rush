package com.example.msbackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum DifficultyLevel {
  LEVEL_1("addition_subtraction"),
  LEVEL_2("mixed_operation"),
  LEVEL_3("linear_equation"),
  LEVEL_4("quadratic_equation"),
  LEVEL_5("trigonometric_equation"),
  LEVEL_6("exponential_logarithmic_equation");

  @EnumValue
  private final String value;

  DifficultyLevel(String value) {
    this.value = value;
  }

  // 根据数据库值获取对应的枚举
  public static DifficultyLevel fromValue(String value) {
    for (DifficultyLevel level : DifficultyLevel.values()) {
      if (level.value.equals(value)) {
        return level;
      }
    }
    throw new IllegalArgumentException("No matching DifficultyLevel for value: " + value);
  }
}