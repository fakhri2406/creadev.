package com.creadev.service.impl;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.external.openai.chat.ChatClient;
import com.creadev.external.openai.chat.ChatMessage;
import com.creadev.external.openai.project.ProjectRequestService;
import com.creadev.external.openai.question.QuestionService;
import com.creadev.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private static final String CLASSIFIER_PROMPT =
        "You are an AI classifier for 'creadev.ai'. Given the user's input, respond with exactly '1' if it's a general question about the company overview, categories, or products; " +
            "respond with exactly '2' if it's a project request describing requirements, budget, contact info, etc. " +
            "Output only '1' or '2' without extra text.";

    private final ChatClient chatClient;
    private final QuestionService questionService;
    private final ProjectRequestService projectRequestService;

    @Override
    public AiResponse handle(AiRequest request) {
        String content = request.question();
        List<ChatMessage> classifyMessages = List.of(
            new ChatMessage("system", CLASSIFIER_PROMPT),
            new ChatMessage("user", content)
        );

        String classification;
        try {
            classification = chatClient.chat(classifyMessages).trim();
        } catch (Exception e) {
            return questionService.getAnswer(request);
        }

        if (classification.startsWith("2")) {
            return projectRequestService.handle(request);
        } else {
            return questionService.getAnswer(request);
        }
    }
} 