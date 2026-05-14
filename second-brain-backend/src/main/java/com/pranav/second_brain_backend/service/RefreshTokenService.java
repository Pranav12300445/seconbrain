package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.exception.UnauthorizedException;
import com.pranav.second_brain_backend.model.RefreshToken;
import com.pranav.second_brain_backend.model.User;
import com.pranav.second_brain_backend.repository.RefreshTokenRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Autowired
    private RefreshTokenRepository repository;

    // 🔹 consider making this configurable via properties
    private static final long REFRESH_TOKEN_DAYS = 7;

    @Transactional
    public RefreshToken createToken(User user) {

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS))
                .user(user)
                .build();

        return repository.save(token);
    }

    public RefreshToken verifyToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            repository.delete(refreshToken); // remove expired
            throw new UnauthorizedException("Refresh token expired");
        }

        return refreshToken;
    }

    // 🔹 single DB call delete (no pre-fetch)
    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }

    // 🔹 used in rotation (no extra lookup)
    @Transactional
    public void delete(RefreshToken token) {
        repository.delete(token);
    }

    // 🔥 useful for "logout all devices"
    @Transactional
    public void deleteAllByUser(Long userId) {
        repository.deleteByUser_Id(userId);
    }

    @Scheduled(cron = "0 0 0 * * ?") // daily midnight
    @Transactional
    public void cleanExpiredTokens() {
        long deleted = repository.deleteByExpiryDateBefore(LocalDateTime.now());
        logger.info("Deleted {} expired refresh tokens", deleted);
    }
}