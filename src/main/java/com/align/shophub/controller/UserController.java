package com.align.shophub.controller;

import com.align.shophub.dto.UserProfileDto;
import com.align.shophub.entity.UserInfo;
import com.align.shophub.repository.UserInfoRepository;
import com.align.shophub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserInfoRepository userRepository;
    @Autowired private JwtService jwtService;

    @GetMapping("/me")
    public UserProfileDto getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}