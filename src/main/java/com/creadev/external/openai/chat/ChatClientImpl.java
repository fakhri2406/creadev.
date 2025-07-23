package com.creadev.external.openai.chat;

import com.creadev.config.openai.OpenAiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static com.creadev.util.ErrorMessages.FAILED_TO_SEND_CHAT_REQUEST;

@Service
@RequiredArgsConstructor
public class ChatClientImpl implements ChatClient {
    private final OpenAiProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String chat(List<ChatMessage> messages) {
        try {
            var arrayNode = objectMapper.createArrayNode();
            for (ChatMessage msg : messages) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("role", msg.role());
                node.put("content", msg.content());
                arrayNode.add(node);
            }

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", properties.getModel());
            payload.set("messages", arrayNode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            String response = restTemplate.postForObject(properties.getBaseUrl() + "/chat/completions", entity, String.class);
            JsonNode root = objectMapper.readTree(response);

            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (IOException e) {
            throw new RuntimeException(FAILED_TO_SEND_CHAT_REQUEST, e);
        }
    }
} 