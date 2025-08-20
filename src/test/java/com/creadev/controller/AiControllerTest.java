package com.creadev.controller;

import com.creadev.dto.request.ai.AiRequest;
import com.creadev.dto.response.ai.AiResponse;
import com.creadev.service.AiService;
import com.creadev.util.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        com.creadev.config.jwt.SecurityConfig.class,
        com.creadev.config.jwt.JwtAuthenticationFilter.class,
        com.creadev.config.jwt.JwtSettings.class
    })
})
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, AiControllerTest.MockConfig.class})
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AiService aiService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        AiService aiService() {
            return Mockito.mock(AiService.class);
        }
    }

    @Test
    @DisplayName("POST /api/v1/ai/ask returns 200 with answer for valid request")
    void ask_valid_returnsOk() throws Exception {
        // Arrange
        AiRequest req = new AiRequest("What do you do?");
        when(aiService.handle(any(AiRequest.class))).thenReturn(new AiResponse("We build software."));

        // Act
        mockMvc.perform(post("/api/v1/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.answer", is("We build software.")));
    }

    @Test
    @DisplayName("POST /api/v1/ai/ask returns 400 when validation fails")
    void ask_invalid_returnsBadRequest() throws Exception {
        // Arrange
        AiRequest invalid = new AiRequest("");

        // Act
        mockMvc.perform(post("/api/v1/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/ai/ask returns 400 on malformed JSON body")
    void ask_malformedJson_returnsBadRequest() throws Exception {
        // Arrange
        // Act
        mockMvc.perform(post("/api/v1/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ not-json }"))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Malformed JSON request")));
    }

    @Test
    @DisplayName("POST /api/v1/ai/ask returns 500 when service throws unexpected error")
    void ask_serviceThrows_returnsInternalServerError() throws Exception {
        // Arrange
        AiRequest req = new AiRequest("Tell me something");
        when(aiService.handle(any(AiRequest.class))).thenThrow(new RuntimeException("AI error"));

        // Act
        mockMvc.perform(post("/api/v1/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            // Assert
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message", is("AI error")));
    }
}


