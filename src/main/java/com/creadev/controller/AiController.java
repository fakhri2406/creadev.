package com.creadev.controller;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.service.AiService;
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
@Tag(
    name = "AI",
    description = "Endpoints for AI assistant"
)
@RequestMapping("/api/v1/ai")
@Validated
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @Operation(
        summary = "Ask AI assistant or handle project requests",
        description = "Handles general AI queries about the company or project requests. " +
            "If the input describes a project request, follow-up questions may be returned, " +
            "and once complete, an email is sent and a confirmation message is returned."
    )
    @PostMapping("/ask")
    public ResponseEntity<AiResponse> ask(@RequestBody @Valid AiRequest request) {
        AiResponse response = aiService.handle(request);
        return ResponseEntity.ok(response);
    }
} 