package com.creadev.controller;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.dto.response.ai.ProjectResponse;
import com.creadev.external.email.EmailService;
import com.creadev.external.openai.project.ProjectParserService;
import com.creadev.external.openai.question.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI", description = "Endpoints for AI assistant")
@RequestMapping("/api/v1/ai")
@Validated
@RequiredArgsConstructor
public class AiController {
    private final QuestionService questionService;
    private final ProjectParserService parserService;
    private final EmailService emailService;

    @Operation(
        summary = "Ask AI assistant",
        description = "Ask the AI assistant questions about the company, categories, and products"
    )
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@RequestBody @Valid AiRequest request) {
        String answer = questionService.getAnswer(request.question());
        return ResponseEntity.ok(new AiResponse(answer));
    }

    @Operation(
        summary = "Parse project request",
        description = "Parses a free-form project request into structured sections and emails the result"
    )
    @PostMapping("/project-request")
    public ResponseEntity<ProjectResponse> projectRequest(@RequestBody @Valid AiRequest request) {
        String parsed = parserService.parse(request.question());
        emailService.sendEmail(
            "creadev.workspace@gmail.com",
            "fakhri.gezalov24@gmail.com",
            "New Project Request",
            parsed
        );
        return ResponseEntity.ok(new ProjectResponse(parsed));
    }
} 