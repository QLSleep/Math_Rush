package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("question_attempts")
public class QuestionAttempt {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("user_id")
  private Long userId;

  @TableField("expression_id")
  private Long expressionId;

  @TableField("session_id")
  private Long sessionId;

  @TableField("start_time")
  private LocalDateTime startTime;

  @TableField("end_time")
  private LocalDateTime endTime;

  @TableField("duration_ms")
  private Integer durationMs;

  @TableField("is_correct")
  private Boolean isCorrect;

  @TableField("submitted_answer")
  private String submittedAnswer;

  @TableField("created_at")
  private LocalDateTime createdAt;
}