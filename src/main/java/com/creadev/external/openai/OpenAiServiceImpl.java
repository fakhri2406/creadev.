package com.creadev.external.openai;

import com.creadev.config.openai.OpenAiProperties;
import com.creadev.dto.response.category.CategoryResponse;
import com.creadev.dto.response.product.ProductResponse;
import com.creadev.service.CategoryService;
import com.creadev.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static com.creadev.util.ErrorMessages.RESPONSE_FAILED;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {
    private static final String SYSTEM_PROMPT = "You are an AI assistant for 'creadev.' ('creadev.ai'), a company specializing in custom software development. Use the provided categories and products to answer user questions about the company. The user may ask a question in any language (most used one is Azerbaijani). You need to provide clear, concise, and helpful responses in the language of user's prompt.";

    private final OpenAiProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public String getAnswer(String question) {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories(Pageable.unpaged()).getContent();
            List<ProductResponse> products = productService.getAllProducts(Pageable.unpaged()).getContent();

            String categoriesJson = objectMapper.writeValueAsString(categories);
            String productsJson = objectMapper.writeValueAsString(products);

            JsonNode payload = objectMapper.createObjectNode()
                .put("model", properties.getModel())
                .set("messages", objectMapper.createArrayNode()
                    .add(objectMapper.createObjectNode()
                        .put("role", "system")
                        .put("content", SYSTEM_PROMPT))
                    .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", "Categories: " + categoriesJson))
                    .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", "Products: " + productsJson))
                    .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", question))
                );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            String response = restTemplate.postForObject(properties.getBaseUrl() + "/chat/completions", requestEntity, String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (IOException | RestClientException e) {
            throw new RuntimeException(RESPONSE_FAILED, e);
        }
    }
} 