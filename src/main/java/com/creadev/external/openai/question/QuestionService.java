package com.creadev.external.openai.question;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;

public interface QuestionService {
    /**
     * Process a user question request and return the AI response.
     *
     * @param request the AiRequest containing the question
     * @return AiResponse with the AI's answer
     */
    AiResponse getAnswer(AiRequest request);
} 