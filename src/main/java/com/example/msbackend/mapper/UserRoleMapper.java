package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.entity.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper extends BaseMapper<UserRole> {

  /**
   * 插入用户角色关联
   * @param userId 用户ID
   * @param roleId 角色ID
   * @return 是否插入成功
   */
  boolean insertUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);
  
  /**
   * 删除用户角色关联
   * @param userId 用户ID
   * @return 是否删除成功
   */
  boolean deleteUserRolesByUserId(@Param("userId") Long userId);

}
