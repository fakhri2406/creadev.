package com.creadev.external.openai.project;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;

public interface ProjectRequestService {
    /**
     * Handle a project request: returns follow-up prompts or confirmation.
     *
     * @param request the AiRequest containing user input
     * @return AiResponse with follow-up questions or confirmation
     */
    AiResponse handle(AiRequest request);
} 