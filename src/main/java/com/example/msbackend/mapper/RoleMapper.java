package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.entity.Role;
import com.example.msbackend.enums.RoleNames;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
  
  /**
   * 根据角色名查询角色信息
   * @param roleName 角色名枚举
   * @return 角色信息
   */
  Role getRoleByName(@Param("roleName") RoleNames roleName);
  
  /**
   * 批量查询角色信息
   * @param roleNames 角色名枚举列表
   * @return 角色信息列表
   */
  List<Role> getRolesByNames(@Param("roleNames") List<RoleNames> roleNames);
  
  /**
   * 根据角色名获取角色ID
   * @param roleName 角色名
   * @return 角色ID
   */
  Integer getRoleIdByRoleName(@Param("roleName") String roleName);
}
