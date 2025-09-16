package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_user_role")
public class UserRole {
//  @TableId(value = "user_id", type = IdType.NONE)
  @TableField(value = "user_id")
  private Long userId;
//  @TableId(value = "role_id", type = IdType.NONE)
@TableField(value = "role_id")
  private Integer roleId;
}
