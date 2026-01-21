package com.align.shophub.controller;

import com.align.shophub.dto.AuthRequest;
import com.align.shophub.dto.AuthResponse;
import com.align.shophub.dto.SigninRequest;
import com.align.shophub.service.JwtService;
import com.align.shophub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService service;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/new")
    public String addNewUser(@RequestBody SigninRequest signinRequest) {
        return service.addUser(signinRequest);
    }

//    @GetMapping("/all")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public List<Product> getAllTheProducts() {
//        return service.getProducts();
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
//    public Product getProductById(@PathVariable int id) {
//        return service.getProduct(id);
//    }


    @PostMapping("/authenticate")
    public AuthResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return AuthResponse.builder().token(jwtService.generateToken(authRequest.getUsername())).build();
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }


    }

}
