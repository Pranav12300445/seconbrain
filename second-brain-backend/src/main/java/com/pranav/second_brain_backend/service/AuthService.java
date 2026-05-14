package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.dto.AuthResponse;
import com.pranav.second_brain_backend.exception.BadRequestException;
import com.pranav.second_brain_backend.exception.UnauthorizedException;
import com.pranav.second_brain_backend.model.RefreshToken;
import com.pranav.second_brain_backend.model.Role;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.UserRepository;
import com.pranav.second_brain_backend.security.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Helper
    private AuthResponse buildResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User register(User user) {

        logger.info("Register attempt for email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration failed - email already exists: {}", user.getEmail());
            throw new BadRequestException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully: {}", savedUser.getEmail());

        return savedUser;
    }

    public AuthResponse registerAndReturn(User user) {

        User savedUser = register(user);

        String accessToken = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name());

        RefreshToken refreshToken = refreshTokenService.createToken(savedUser);

        logger.info("Tokens generated for new user: {}", savedUser.getEmail());

        return buildResponse(savedUser, accessToken, refreshToken.getToken());
    }

    public AuthResponse login(String email, String password) {

        logger.info("User login attempt: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}", email);
                    return new UnauthorizedException("Invalid credentials");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed - wrong password: {}", email);
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

        RefreshToken refreshToken = refreshTokenService.createToken(user);

        logger.info("User logged in successfully: {}", email);

        return buildResponse(user, accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String refreshToken) {

        logger.info("Token refresh attempt");

        RefreshToken oldToken = refreshTokenService.verifyToken(refreshToken);
        User user = oldToken.getUser();

        refreshTokenService.delete(oldToken);

        RefreshToken newRefreshToken = refreshTokenService.createToken(user);

        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

        logger.info("Token refreshed for user: {}", user.getEmail());

        return buildResponse(user, newAccessToken, newRefreshToken.getToken());
    }

    public void logout(String refreshToken) {

        logger.info("Logout attempt");

        RefreshToken token = refreshTokenService.verifyToken(refreshToken);

        refreshTokenService.delete(token);

        logger.info("User logged out: {}", token.getUser().getEmail());
    }
}