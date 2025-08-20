package com.creadev.controller;

import com.creadev.dto.request.auth.LoginRequest;
import com.creadev.dto.request.auth.RefreshTokenRequest;
import com.creadev.dto.response.auth.AuthResponse;
import com.creadev.dto.response.auth.UserInfoResponse;
import com.creadev.service.AuthService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        com.creadev.config.jwt.SecurityConfig.class,
        com.creadev.config.jwt.JwtAuthenticationFilter.class,
        com.creadev.config.jwt.JwtSettings.class
    })
})
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, AuthControllerTest.MockConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 200 with tokens")
    void login_valid_returnsOk() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john", "P@ssw0rd!");
        when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("access", "refresh"));

        // Act
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", is("access")))
            .andExpect(jsonPath("$.refreshToken", is("refresh")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 400 on validation errors")
    void login_invalid_returnsBadRequest() throws Exception {
        // Arrange
        LoginRequest invalid = new LoginRequest("", "");

        // Act
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 401 on bad credentials")
    void login_badCredentials_returnsUnauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("john", "wrong");
        when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", is("Invalid credentials")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh returns 200 with new tokens")
    void refresh_valid_returnsOk() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("refresh");
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(new AuthResponse("newAccess", "newRefresh"));

        // Act
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", is("newAccess")))
            .andExpect(jsonPath("$.refreshToken", is("newRefresh")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh returns 400 on validation errors")
    void refresh_invalid_returnsBadRequest() throws Exception {
        // Arrange
        RefreshTokenRequest invalid = new RefreshTokenRequest("");

        // Act
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            // Assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Validation failed")))
            .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me returns 401 when header missing or invalid")
    void me_missingHeader_returnsUnauthorized() throws Exception {
        // Arrange
        // Act
        mockMvc.perform(get("/api/v1/auth/me"))
            // Assert
            .andExpect(status().isUnauthorized());

        // Arrange
        // Act
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Token abc"))
            // Assert
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/auth/me returns 200 with user info when header present")
    void me_validHeader_returnsOk() throws Exception {
        // Arrange
        UserInfoResponse info = new UserInfoResponse(
            "issuer", "aud", "sub", new Date(1000), new Date(2000),
            "john", "John", "Doe", "john@example.com", "501234567", "ADMIN"
        );
        when(authService.getUserInfo(eq("token123"))).thenReturn(info);

        // Act
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer token123"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is("john")))
            .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout returns 204 and calls service with token when header present")
    void logout_withHeader_returnsNoContent() throws Exception {
        // Arrange
        // Act
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer token123"))
            // Assert
            .andExpect(status().isNoContent());

        verify(authService).logout("token123");
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout returns 204 and calls service with null when header missing")
    void logout_withoutHeader_returnsNoContent() throws Exception {
        // Arrange
        // Act
        mockMvc.perform(post("/api/v1/auth/logout"))
            // Assert
            .andExpect(status().isNoContent());

        verify(authService).logout(null);
    }
}
