package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.dto.CancelUserDTO;
import com.example.msbackend.dto.ChangePwdDTO;
import com.example.msbackend.dto.InsertUserDTO;
import com.example.msbackend.dto.ModifyUserInfoDTO;
import com.example.msbackend.entity.User;
import com.example.msbackend.vo.UserInfoVO;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

  UserInfoVO getUserInfo(@Param("username") String userName);

  boolean insertUser(InsertUserDTO insertUserDTO);
  
  /**
   * 检查用户名或邮箱是否已存在
   * @param username 用户名
   * @param email 邮箱
   * @return 是否存在
   */
  boolean checkUsernameOrEmailExists(@Param("username") String username, @Param("email") String email);

  /**
   * 根据用户名和邮箱查询用户ID
   * @param username 用户名
   * @param email 邮箱
   * @return 用户ID，如果不存在则返回null
   */
  Long getUserByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

  /**
   * 检查新用户名是否已被其他用户使用（排除当前用户）
   * @param currentUsername 当前用户名
   * @param currentEmail 当前邮箱
   * @param newUsername 新用户名
   * @return 是否被使用
   */
  boolean checkUsernameExistsExcludeCurrent(@Param("currentUsername") String currentUsername, 
                                          @Param("currentEmail") String currentEmail, 
                                          @Param("newUsername") String newUsername);

  /**
   * 检查新邮箱是否已被其他用户使用（排除当前用户）
   * @param currentUsername 当前用户名
   * @param currentEmail 当前邮箱
   * @param newEmail 新邮箱
   * @return 是否被使用
   */
  boolean checkEmailExistsExcludeCurrent(@Param("currentUsername") String currentUsername, 
                                        @Param("currentEmail") String currentEmail, 
                                        @Param("newEmail") String newEmail);

  /**
   * 更新用户信息
   * @param updateUserDTO 用户更新信息对象
   * @return 更新是否成功
   */
  boolean updateUserInfo(ModifyUserInfoDTO updateUserDTO);

  ChangePwdDTO getChangePwdDTO(@Param("username") String username);

  boolean changePwd(ChangePwdDTO changePwdDTO);

  CancelUserDTO getCancelUserDTO(@Param("username") String username);

  boolean setUserStatus(@Param("id") Long userId, @Param("status") byte userStatus);
  
  /**
   * 物理删除用户
   * @param id 用户ID
   * @return 删除是否成功
   */
  boolean deleteUserById(@Param("id") Long id);
  

}
