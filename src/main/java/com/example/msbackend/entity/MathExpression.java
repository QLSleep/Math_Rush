package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.msbackend.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("math_expressions")
public class MathExpression {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;
  @TableField("expression")
  private String expression;
  @TableField("type")
  private DifficultyLevel difficultyLevel;
  @TableField("create_at")
  private LocalDateTime createAt;
  @TableField(exist = false)
  private List<MathAnswer> answers;
}
