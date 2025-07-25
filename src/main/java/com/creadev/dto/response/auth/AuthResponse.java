package com.creadev.dto.response.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthResponse(
    @NotBlank String accessToken,
    @NotBlank String refreshToken
) {
}