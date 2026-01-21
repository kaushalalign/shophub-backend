package com.align.shophub.service;


import com.align.shophub.dto.SigninRequest;
import com.align.shophub.entity.UserInfo;
import com.align.shophub.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String addUser(SigninRequest signinRequest) {
        UserInfo user = new UserInfo();
        user.setEmail(signinRequest.getEmail());
        user.setName(signinRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signinRequest.getPassword()));
        user.setRoles(signinRequest.getRoles());

        repository.save(user);
        return "user added to system ";
    }
}
