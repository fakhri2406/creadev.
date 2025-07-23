package com.creadev.controller;

import com.creadev.dto.request.auth.LoginRequest;
import com.creadev.dto.request.auth.RefreshTokenRequest;
import com.creadev.dto.response.auth.AuthResponse;
import com.creadev.dto.response.auth.UserInfoResponse;
import com.creadev.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(
    name = "Auth",
    description = "Endpoints for authentication"
)
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
        summary = "Get current authenticated user",
        description = "Retrieve information about the current user based on the Authorization header",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith("bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = header.substring(header.indexOf(' ') + 1);
        UserInfoResponse response = authService.getUserInfo(token);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Authenticate a user and obtain tokens",
        description = "Log in with a username and password to receive access and refresh tokens"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Refresh authentication tokens",
        description = "Refresh access and refresh tokens using a valid refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Log out the current user",
        description = "Revoke the current user's access token",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(header.indexOf(' ') + 1);
            authService.logout(token);
        } else {
            authService.logout(null);
        }
        return ResponseEntity.noContent().build();
    }
} 
