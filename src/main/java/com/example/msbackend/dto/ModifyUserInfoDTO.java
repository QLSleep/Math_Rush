package com.example.msbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserInfoDTO {
    private Long id;             // 用户ID
    private String username;     // 当前用户名
    private String email;        // 当前邮箱
    private String newUsername;  // 新用户名
    private String newEmail;     // 新邮箱
}
