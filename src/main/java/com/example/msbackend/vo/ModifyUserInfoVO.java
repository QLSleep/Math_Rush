package com.example.msbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserInfoVO {
  private String username;
  private String email;
  private String newUsername;
  private String newEmail;
}
