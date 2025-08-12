package com.creadev.config.jwt;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EDITOR = "EDITOR";

    private static final String USERS_URL = "/api/v1/users/**";
    private static final String CATEGORIES_URL = "/api/v1/categories/**";
    private static final String PRODUCTS_URL = "/api/v1/products/**";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, USERS_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, USERS_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, USERS_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, USERS_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, CATEGORIES_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, CATEGORIES_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, CATEGORIES_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, PRODUCTS_URL).hasAnyRole(ROLE_ADMIN, ROLE_EDITOR)
                .requestMatchers(HttpMethod.PUT, PRODUCTS_URL).hasAnyRole(ROLE_ADMIN, ROLE_EDITOR)
                .requestMatchers(HttpMethod.DELETE, PRODUCTS_URL).hasRole(ROLE_ADMIN)
                .requestMatchers(
                    "/",
                    "/actuator/health",
                    "/actuator/health/**",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/ai/ask",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers(
                    HttpMethod.GET,
                    CATEGORIES_URL,
                    PRODUCTS_URL
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}