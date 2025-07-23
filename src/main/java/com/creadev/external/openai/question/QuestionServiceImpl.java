package com.creadev.external.openai.question;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.external.openai.chat.ChatClient;
import com.creadev.external.openai.chat.ChatMessage;
import com.creadev.service.CategoryService;
import com.creadev.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.creadev.util.ErrorMessages.FAILED_TO_PREPARE_AI_REQUEST;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private static final String SYSTEM_PROMPT = "You are an AI assistant for 'creadev.' ('creadev.ai'), a company specializing in custom software development. Use the provided categories and products to answer user questions about the company. The user may ask a question in any language (most used one is Azerbaijani). You need to provide clear, concise, and helpful responses in the language of user's prompt.";
    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";

    private final ChatClient chatClient;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Override
    public AiResponse getAnswer(AiRequest request) {
        String question = request.question();
        try {
            String categoriesJson = objectMapper.writeValueAsString(
                categoryService.getAllCategories(Pageable.unpaged()).getContent()
            );
            String productsJson = objectMapper.writeValueAsString(
                productService.getAllProducts(Pageable.unpaged()).getContent()
            );

            List<ChatMessage> messages = List.of(
                new ChatMessage(ROLE_SYSTEM, SYSTEM_PROMPT),
                new ChatMessage(ROLE_SYSTEM, "Categories: " + categoriesJson),
                new ChatMessage(ROLE_SYSTEM, "Products: " + productsJson),
                new ChatMessage(ROLE_USER, question)
            );

            String answer = chatClient.chat(messages);
            return new AiResponse(answer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(FAILED_TO_PREPARE_AI_REQUEST, e);
        }
    }
} 