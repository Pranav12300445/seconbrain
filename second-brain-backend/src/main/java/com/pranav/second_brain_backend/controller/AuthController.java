package com.pranav.second_brain_backend.controller;

import com.pranav.second_brain_backend.dto.AuthResponse;
import com.pranav.second_brain_backend.dto.LoginRequest;
import com.pranav.second_brain_backend.dto.LogoutRequest;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody User user) {
        return authService.registerAndReturn(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody LogoutRequest request) {
        System.out.println("REFRESH TOKEN: " + request.getRefreshToken());
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return "Logged out successfully";
    }
}