package com.creadev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Health",
    description = "Endpoints for health checks"
)
@RestController
public class HealthController {

    @Operation(
        summary = "Ping health check",
        description = "Returns 'pong' if the application is running"
    )
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
} 