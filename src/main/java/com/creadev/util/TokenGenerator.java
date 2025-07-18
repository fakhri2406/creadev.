package com.creadev.util;

import com.creadev.config.jwt.JwtProperties;
import com.creadev.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenGenerator {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenGenerator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusSeconds(jwtProperties.getAccessTokenValiditySeconds()));

        return Jwts.builder()
            .setIssuer(jwtProperties.getIssuer())
            .setAudience(jwtProperties.getAudience())
            .setSubject(user.getUsername())
            .claim("username", user.getUsername())
            .claim("firstName", user.getFirstName())
            .claim("lastName", user.getLastName())
            .claim("email", user.getEmail())
            .claim("phoneNumber", user.getPhoneNumber())
            .claim("role", user.getRole().getTitle())
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
} 