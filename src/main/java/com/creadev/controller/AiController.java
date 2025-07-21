package com.creadev.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.external.openai.OpenAiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "AI", description = "Endpoints for AI assistant")
@RequestMapping("/api/v1/ai")
@Validated
@RequiredArgsConstructor
public class AiController {
    private final OpenAiService openAiService;

    @Operation(
        summary = "Ask AI assistant",
        description = "Ask the AI assistant questions about the company, categories, and products"
    )
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@RequestBody @Valid AiRequest request) {
        String answer = openAiService.getAnswer(request.question());
        return ResponseEntity.ok(new AiResponse(answer));
    }
} 