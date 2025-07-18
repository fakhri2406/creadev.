package com.creadev.dto.response.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserResponse(
    @NotNull Integer id,
    @NotBlank String username,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Email String email,
    @NotBlank String phoneNumber,
    @NotNull Instant registeredAt,
    Instant lastLoginAt,
    @NotNull Integer roleId,
    @NotBlank String role
) {
} 