package com.creadev.dto.request.ai;

import jakarta.validation.constraints.NotBlank;

public record AiRequest(
    @NotBlank(message = "Question is required")
    String question
) {
} 