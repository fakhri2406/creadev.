package com.creadev.dto.response.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ProductResponse(
    @NotNull Integer id,
    @NotNull Integer categoryId,
    @NotBlank String title,
    @NotBlank String description,
    @NotBlank String link,
    @NotBlank String imageUrl,
    @NotNull Instant createdAt,
    @NotNull Instant updatedAt
) {
}