package com.creadev.external.openai.chat;

public record ChatMessage(
    String role,

    String content
) {
} 