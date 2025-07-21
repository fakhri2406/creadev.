package com.creadev.dto.response.ai;

import jakarta.validation.constraints.NotBlank;

public record AiResponse(
    @NotBlank String answer
) {
} 