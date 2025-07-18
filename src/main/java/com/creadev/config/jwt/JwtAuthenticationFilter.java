package com.creadev.config.jwt;

import com.creadev.repository.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.creadev.util.ErrorMessages.ACCESS_TOKEN_REVOKED;
import static com.creadev.util.ErrorMessages.INVALID_ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final com.creadev.config.jwt.JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(header.indexOf(' ') + 1);
            if (revokedTokenRepository.findByToken(token).isPresent()) {
                throw new AuthenticationServiceException(ACCESS_TOKEN_REVOKED);
            }

            SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims;
            try {
                claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .requireAudience(jwtProperties.getAudience())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            } catch (JwtException e) {
                throw new AuthenticationServiceException(INVALID_ACCESS_TOKEN);
            }

            String username = claims.getSubject();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<String> roles = claims.get("roles", List.class);
                UserDetails userDetails;
                if (roles != null) {
                    userDetails = new org.springframework.security.core.userdetails.User(
                        username,
                        "",
                        roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList()
                    );
                } else {
                    userDetails = userDetailsService.loadUserByUsername(username);
                }
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
} 