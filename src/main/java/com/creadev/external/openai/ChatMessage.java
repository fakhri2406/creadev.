package com.creadev.external.openai;

public record ChatMessage(
    String role,

    String content
) {
} 