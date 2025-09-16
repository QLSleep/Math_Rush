package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRole> {
  
  /**
   * 根据用户ID查询角色ID列表
   * @param userId 用户ID
   * @return 角色ID列表
   */
  List<Integer> getRoleIdsByUserId(@Param("userId") Long userId);
  
  /**
   * 根据角色ID查询用户ID列表
   * @param roleId 角色ID
   * @return 用户ID列表
   */
  List<Long> getUserIdsByRoleId(@Param("roleId") Integer roleId);
  
  /**
   * 根据用户ID和角色ID删除用户角色关联
   * @param userId 用户ID
   * @param roleId 角色ID
   * @return 是否删除成功
   */
  boolean deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
  
  /**
 * 根据用户ID删除所有用户角色关联
 * @param userId 用户ID
 * @return 是否删除成功
 */
  boolean deleteByUserId(@Param("userId") Long userId);
  
  /**
   * 插入用户角色关联
   * @param userId 用户ID
   * @param roleId 角色ID
   * @return 是否插入成功
   */
  boolean insertUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);
  

}
