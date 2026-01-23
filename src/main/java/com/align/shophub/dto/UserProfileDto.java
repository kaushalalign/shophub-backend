package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private String name;
    private String email;
    private String roles;
}