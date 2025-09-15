package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.msbackend.enums.SessionStatus;
import com.example.msbackend.enums.SessionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user_session")
public class UserSession {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("user_id")
  private Long userId;

  @TableField("session_type")
  private SessionType sessionType;

  @TableField("match_id")
  private Long matchId;

  @TableField("start_time")
  private LocalDateTime startTime;

  @TableField("end_time")
  private LocalDateTime endTime;

  @TableField("total_duration_ms")
  private Integer totalDurationMs;

  @TableField("status")
  private SessionStatus status;

  @TableField("create_at")
  private LocalDateTime createAt;
}