package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_role_permission")
public class RolePermission {
  @TableId(value = "roleId", type = IdType.NONE)
  private Integer roleId;
  @TableId(value = "permissionId", type = IdType.NONE)
  private Integer permissionId;
}
