package com.pranav.second_brain_backend.controller;

import com.pranav.second_brain_backend.dto.AuthResponse;
import com.pranav.second_brain_backend.dto.LoginRequest;
import com.pranav.second_brain_backend.dto.LogoutRequest;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.UserRepository;
import com.pranav.second_brain_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(AuthResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build()))
                .orElse(ResponseEntity.status(401).body("User not found"));
    }
}