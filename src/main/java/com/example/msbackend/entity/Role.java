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
@TableName("sys_role")
public class Role {
  @TableId(value ="id", type = IdType.AUTO)
  private Integer id;
  @TableField("name")
  private String name;
  @TableField("description")
  private String description;
}
