package com.creadev.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Token is required")
    String token
) {
}