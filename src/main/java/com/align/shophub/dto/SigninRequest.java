package com.align.shophub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {
    private String username;
    private String password;
    private String email;
    private String roles;
}
