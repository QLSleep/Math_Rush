package com.example.msbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.msbackend.enums.PermissionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("sys_permission")
public class Permission {
  @TableId(value = "id",type = IdType.AUTO)
  private Integer id;
  @TableField("code")
  private PermissionCode code;
  @TableField("description")
  private String description;
}
