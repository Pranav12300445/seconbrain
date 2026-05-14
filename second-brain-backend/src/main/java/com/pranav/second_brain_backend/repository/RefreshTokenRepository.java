package com.pranav.second_brain_backend.repository;

import com.pranav.second_brain_backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUser_Id(Long userId);

    List<RefreshToken> findByUser_Id(Long userId);

    long deleteByExpiryDateBefore(LocalDateTime now); // ✅ returns count
}