package com.creadev.service;

import com.creadev.dto.request.auth.LoginRequest;
import com.creadev.dto.request.auth.RefreshTokenRequest;
import com.creadev.dto.response.auth.AuthResponse;
import com.creadev.dto.response.auth.UserInfoResponse;

public interface AuthService {
    /**
     * Authenticate a user by verifying credentials, invalidating existing refresh tokens, and issuing new tokens.
     *
     * @param loginRequest the login request containing username and password
     * @return an AuthResponse containing access and refresh tokens
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Parse the provided JWT access token to extract and return user information from its claims.
     *
     * @param accessToken the JWT access token
     * @return a UserInfoResponse containing user details
     */
    UserInfoResponse getUserInfo(String accessToken);

    /**
     * Validate and rotate the provided refresh token, generating and returning new access and refresh tokens.
     *
     * @param refreshTokenRequest the request containing the refresh token
     * @return an AuthResponse containing new access and refresh tokens
     */
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * Log out the user by deleting stored refresh tokens and revoking the provided access token.
     *
     * @param accessToken the JWT access token to revoke
     */
    void logout(String accessToken);
} 