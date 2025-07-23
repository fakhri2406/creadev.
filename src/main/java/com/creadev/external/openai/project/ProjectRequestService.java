package com.creadev.external.openai.project;

import com.creadev.dto.response.ai.AiResponse;

public interface ProjectRequestService {
    /**
     * Handle a free-form project request prompt: either returns follow-up questions or emails the structured output and returns a confirmation message.
     *
     * @param request the user's project request text
     * @return an AI response containing either follow-up prompts or confirmation
     */
    AiResponse handle(String request);
} 