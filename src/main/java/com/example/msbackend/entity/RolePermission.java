package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_role_permission")
public class RolePermission {
  @TableField("roleId")
  private Integer roleId;
  @TableField("permissionId")
  private Integer permissionId;
}
