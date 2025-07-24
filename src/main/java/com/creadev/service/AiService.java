package com.creadev.service;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;

public interface AiService {
    /**
     * Process an AI request and return the appropriate response.
     *
     * @param request the AI request DTO
     * @return AI response DTO
     */
    AiResponse handle(AiRequest request);
} 