package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("math_answers")
public class MathAnswer {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;
  @TableField("expression_id")
  private Long expressionId;
  @TableField("answer_key")
  private String answerKey;
  @TableField("float_val")
  private BigDecimal floatVal;
  @TableField("latex_str")
  private String latexStr;
}