package com.creadev.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.featuresToEnable(
            JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS
        );
    }
} 