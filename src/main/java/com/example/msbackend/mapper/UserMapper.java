package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.dto.InsertUserDTO;
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
}
