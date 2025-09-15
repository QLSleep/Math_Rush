package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.msbackend.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user")
public class User {
  @TableId(value = "id",type = IdType.AUTO)
  private Long id;
  @TableField("username")
  private String username;
  @TableField("password")
  private String password;
  @TableField("email")
  private String email;
  @TableField("status")
  private UserStatus status;
  @TableField("created_at")
  private LocalDateTime createAt;
  @TableField("updated_at")
  private LocalDateTime updateAt;
}
