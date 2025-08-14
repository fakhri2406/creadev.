package com.creadev.external.openai.project;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.external.email.EmailService;
import com.creadev.external.openai.chat.ChatClient;
import com.creadev.external.openai.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectRequestServiceImpl implements ProjectRequestService {
    private static final String PARSING_SYSTEM_PROMPT = "You are an AI assistant for 'creadev.' (creadev.ai). You must parse the user's freeform project request into exactly 5 or 6 structured sections: " +
        "1) Project Category (choose one of: Corporate website, Functional platform, Mobile application, Other), " +
        "2) Project Overview (clear and concise description), " +
        "3) Project Features (bulleted list of features), " +
        "4) Project Flows (detailed user and/or admin flows), " +
        "5) Budget (optional, only if the user provided a budget), " +
        "6) Contact Information (phone number, Instagram, or other contact info). " +
        "If any required information (except budget) is missing from the user's prompt (e.g. contact info) or is not enough to build a section (e.g. user/admin flows), ask follow-up questions in the language of user's prompt to obtain it. " +
        "Output only the structured sections with their headings numbered exactly as above.";
    private static final String CONFIRMATION_MESSAGE = "Təşəkkür edirik! Sorğunuz e-mailimizə göndərildi, sizinlə tezliklə əlaqə saxlanılacaq." +
        "\n\n---\n\n" +
        "Thank you! Your request has been sent to our email, you will be contacted very shortly";
    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";

    private final ChatClient chatClient;
    private final EmailService emailService;

    @Override
    public AiResponse handle(AiRequest request) {
        String userInput = request.question();
        String parsed = parse(userInput);
        boolean isStructured = parsed.contains("1)") && parsed.contains("2)");
        if (isStructured) {
            emailService.sendEmail(
                "creadev.workspace@gmail.com",
                "fakhri.gezalov24@gmail.com",
                "New Project Request",
                parsed
            );

            return new AiResponse(CONFIRMATION_MESSAGE);
        } else {
            return new AiResponse(parsed);
        }
    }

    private String parse(String request) {
        List<ChatMessage> messages = List.of(
            new ChatMessage(ROLE_SYSTEM, PARSING_SYSTEM_PROMPT),
            new ChatMessage(ROLE_USER, request)
        );
        return chatClient.chat(messages);
    }
} 