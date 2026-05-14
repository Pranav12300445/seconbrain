package com.pranav.second_brain_backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;
    private String refreshToken;
}