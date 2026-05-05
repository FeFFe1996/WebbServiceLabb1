package com.example.webbservicelabb1;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;


@Service
public class ChatService {

    private final RestClient restClient;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatService(RestClient.Builder restClientBuilder, @Value("${API_KEY}") String apiKey) {
        this.restClient = restClientBuilder
                .clone() // Creates a safe, independent copy of the builder
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "https://myapp.example")
                .defaultHeader("X-Title", "WebbServiceLabb1")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<ChatMessage> sessionMessages() {
        return chatMessages;
    }

    public void sendMsg(@Valid ChatRequest request) {
        ChatMessage userMsg = new ChatMessage(request.getSessionId(), request.getMessage());
        ChatMessage replyMsg = post(request);
        chatMessages.add(userMsg);
        chatMessages.add(replyMsg);
    }

    public ChatMessage post(ChatRequest request) {
        try {
            ObjectMapper objMapper = new ObjectMapper();

            String json = objMapper.writeValueAsString(request);
            var response = restClient.post()
                    .uri("chat/completions")
                    .body(json)
                    .retrieve()
                    .body(ChatMessage.class);
            return new ChatMessage(response.sessionId(), request.getMessage());
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to post message to AI service", e);
        }
    }
}
