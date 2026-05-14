package com.pranav.second_brain_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LogoutRequest {
    private String refreshToken;
}