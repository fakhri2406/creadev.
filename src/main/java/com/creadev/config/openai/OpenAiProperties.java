package com.creadev.config.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@ConfigurationProperties(prefix = "openai")
@Validated
@Data
public class OpenAiProperties {
    @NotBlank
    private String baseUrl;

    @NotBlank
    private String model;

    @NotBlank
    private String apiKey;
} 