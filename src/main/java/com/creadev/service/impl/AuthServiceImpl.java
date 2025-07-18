package com.creadev.service.impl;

import com.creadev.config.jwt.JwtProperties;
import com.creadev.domain.RefreshToken;
import com.creadev.domain.RevokedToken;
import com.creadev.domain.User;
import com.creadev.dto.request.auth.LoginRequest;
import com.creadev.dto.request.auth.RefreshTokenRequest;
import com.creadev.dto.response.auth.AuthResponse;
import com.creadev.dto.response.auth.UserInfoResponse;
import com.creadev.repository.RefreshTokenRepository;
import com.creadev.repository.RevokedTokenRepository;
import com.creadev.repository.UserRepository;
import com.creadev.service.AuthService;
import com.creadev.util.Hasher;
import com.creadev.util.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static com.creadev.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final Hasher hasher;
    private final TokenGenerator tokenGenerator;
    private final JwtProperties jwtProperties;

    /**
     * Read
     */
    @Override
    @Transactional
    public AuthResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));

        String salt = user.getPasswordSalt();
        if (!hasher.matches(request.password() + salt, user.getPasswordHash())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        List<RefreshToken> existingTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!existingTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(existingTokens);
        }

        user.setLastLoginAt(LocalDateTime.now());

        return createAuthResponse(user);
    }

    @Override
    public UserInfoResponse getUserInfo(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException(INVALID_ACCESS_TOKEN);
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(accessToken)
            .getBody();

        return new UserInfoResponse(
            claims.getIssuer(),
            claims.getAudience(),
            claims.getSubject(),
            claims.getIssuedAt(),
            claims.getExpiration(),
            claims.get("username", String.class),
            claims.get("firstName", String.class),
            claims.get("lastName", String.class),
            claims.get("email", String.class),
            claims.get("phoneNumber", String.class),
            claims.get("role", String.class)
        );
    }

    /**
     * Update
     */
    @Override
    @Transactional
    public AuthResponse refreshToken(@Valid RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.token())
            .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CredentialsExpiredException(REFRESH_TOKEN_EXPIRED);
        }

        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUsername.equals(user.getUsername())) {
            throw new AccessDeniedException(REFRESH_TOKEN_MISMATCH);
        }

        String newRefreshTokenStr = tokenGenerator.generateRefreshToken();
        LocalDateTime newExpires = LocalDateTime.ofInstant(
            Instant.now().plusSeconds(jwtProperties.getRefreshTokenValiditySeconds()),
            ZoneOffset.UTC);
        refreshToken.setToken(newRefreshTokenStr);
        refreshToken.setExpiresAt(newExpires);
        refreshTokenRepository.save(refreshToken);

        String newAccessToken = tokenGenerator.generateAccessToken(user);
        return new AuthResponse(newAccessToken, newRefreshTokenStr);
    }

    /**
     * Delete
     */
    @Override
    @Transactional
    public void logout(String accessToken) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));

        List<RefreshToken> existingTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!existingTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(existingTokens);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

            Date expiryDate = claims.getExpiration();
            LocalDateTime expiresAt = LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneOffset.UTC);

            RevokedToken revoked = RevokedToken.builder()
                .token(accessToken)
                .expiresAt(expiresAt)
                .build();
            revokedTokenRepository.save(revoked);
        }
    }

    /**
     * Helper
     */
    private AuthResponse createAuthResponse(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user);
        String refreshTokenStr = tokenGenerator.generateRefreshToken();

        LocalDateTime refreshExpires = LocalDateTime.ofInstant(
            Instant.now().plusSeconds(jwtProperties.getRefreshTokenValiditySeconds()),
            ZoneOffset.UTC);

        RefreshToken refreshToken = RefreshToken.builder()
            .token(refreshTokenStr)
            .expiresAt(refreshExpires)
            .userId(user.getId())
            .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr);
    }
} 
