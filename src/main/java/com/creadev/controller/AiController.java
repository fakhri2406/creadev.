package com.creadev.controller;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.external.openai.project.ProjectRequestService;
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
    private final ProjectRequestService projectRequestService;

    @Operation(
        summary = "Ask AI assistant",
        description = "Ask the AI assistant questions about the company, categories, and products"
    )
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@RequestBody @Valid AiRequest request) {
        AiResponse response = questionService.getAnswer(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Parse project request",
        description = "Parses a project request: if details are missing, returns follow-up questions; once complete, emails the report and returns confirmation."
    )
    @PostMapping("/project-request")
    public ResponseEntity<AiResponse> projectRequest(@RequestBody @Valid AiRequest request) {
        AiResponse response = projectRequestService.handle(request);
        return ResponseEntity.ok(response);
    }
} 