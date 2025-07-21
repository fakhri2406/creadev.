package com.creadev.config.openai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
@RequiredArgsConstructor
public class OpenAiConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 