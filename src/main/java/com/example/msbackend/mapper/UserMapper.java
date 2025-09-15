package com.example.msbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.msbackend.dto.UserInfoDTO;
import com.example.msbackend.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

  UserInfoDTO getUserInfo(@Param("username") String userName);
}
