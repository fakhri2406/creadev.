package com.creadev.external.openai.chat;

import java.util.List;

public interface ChatClient {
    /**
     * Send a list of chat messages to OpenAI and retrieve the assistant's response.
     *
     * @param messages system and user messages
     * @return the AI-generated response
     */
    String chat(List<ChatMessage> messages);
} 