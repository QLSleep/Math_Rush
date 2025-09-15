package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.msbackend.enums.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("matches")
public class Match {
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;
  @TableField("status")
  private MatchStatus status;
  @TableField("start_time")
  private LocalDateTime startTime;
  @TableField("end_time")
  private LocalDateTime endTime;
  @TableField("total_duration_ms")
  private Integer totalDurationMs;
  @TableField("winner_id")
  private Long winnerId;
  @TableField("created_at")
  private LocalDateTime createdAt;
}
