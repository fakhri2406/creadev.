package com.creadev.external.openai.project;

import com.creadev.external.openai.chat.ChatClient;
import com.creadev.external.openai.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectParserServiceImpl implements ProjectParserService {
    private static final String PARSING_SYSTEM_PROMPT = "You are an AI assistant for 'creadev.' (creadev.ai). You must parse the user's freeform project request into exactly 5 or 6 structured sections: " +
            "1) Project Category (choose one of: Corporate website, Functional platform, Mobile application, Other), " +
            "2) Project Overview (clear and concise description), " +
            "3) Project Features (bulleted list of features), " +
            "4) Project Flows (detailed user and admin flows), " +
            "5) Budget (optional, only if the user provided a budget), " +
            "6) Contact Information (phone number, Instagram, or other contact info). " +
            "If any required information (except budget) is missing from the user's prompt (e.g. contact info) or is not enough to build a section (e.g. user/admin flows), ask follow-up questions to obtain it. " +
            "Output only the structured sections with their headings numbered exactly as above.";

    private final ChatClient chatClient;

    @Override
    public String parse(String request) {
        List<ChatMessage> messages = List.of(
            new ChatMessage("system", PARSING_SYSTEM_PROMPT),
            new ChatMessage("user", request)
        );
        return chatClient.chat(messages);
    }
} 