package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.msbackend.enums.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("match_participants")
public class MatchParticipant {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  @TableField("match_id")
  private Long matchId;

  @TableField("user_id")
  private Long userId;

  @TableField("joined_at")
  private LocalDateTime joinedAt;

  @TableField("score")
  private Integer score;

  @TableField("total_duration_ms")
  private Integer totalDurationMs;

  @TableField("status")
  private ParticipantStatus status;
}