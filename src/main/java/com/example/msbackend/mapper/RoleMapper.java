package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.entity.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {

  /**
   * 根据角色名获取角色ID
   * @param roleName 角色名
   * @return 角色ID
   */
  Integer getRoleIdByRoleName(@Param("roleName") String roleName);
}
