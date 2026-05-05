package com.example.webbservicelabb1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ApiVersionInserter;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final RestClient restClient;
    private List<ChatMessage> chatMessages;

    public ChatService(RestClient.Builder restClientBuilder, @Value("${API_KEY}") String apiKey) {
        this.restClient = restClientBuilder
                .clone() // Creates a safe, independent copy of the builder
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "https://myapp.example")
                .defaultHeader("X-Title", "WebbServiceLabb1")
                .build();
    }

    public record ChatResponse(List<Choice> choices){}

    public List<ChatMessage> sessionMessages() {
        if (chatMessages.isEmpty())
            chatMessages = new ArrayList<>();
        return chatMessages;
    }
}
