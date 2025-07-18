package com.creadev.dto.response.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record UserInfoResponse(
    @NotBlank String issuer,
    @NotBlank String audience,
    @NotBlank String subject,
    @NotNull Date issuedAt,
    @NotNull Date expiration,
    @NotBlank String username,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String email,
    @NotBlank String phoneNumber,
    @NotBlank String role
) {
}