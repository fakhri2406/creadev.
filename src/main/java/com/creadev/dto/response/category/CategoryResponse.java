package com.creadev.dto.response.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CategoryResponse(
    @NotNull Integer id,
    @NotBlank String title,
    String description,
    @NotNull Instant createdAt
) {
}