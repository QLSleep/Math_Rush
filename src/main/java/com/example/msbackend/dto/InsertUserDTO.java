package com.example.msbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsertUserDTO {
  private Long id;
  private String username;
  private String password;
  private String email;
  private List<String> roles; //角色名
}
