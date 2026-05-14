package com.pranav.second_brain_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_token", columnList = "token"),
        @Index(name = "idx_expiry", columnList = "expiryDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ unique + indexed (fast lookup)
    @Column(unique = true, nullable = false, length = 500)
    private String token;

    // ✅ indexed for cleanup job
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // ✅ lazy loading (good)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}