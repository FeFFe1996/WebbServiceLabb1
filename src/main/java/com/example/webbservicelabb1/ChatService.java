package com.example.webbservicelabb1;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ApiVersionInserter;
import org.springframework.web.client.RestClient;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .build();
    }

    public List<ChatMessage> sessionMessages() {
        return chatMessages;
    }

    public void sendMsg(@Valid ChatRequest request) {
        ChatMessage chatMsg = new ChatMessage(request.getSessionId(), request.getMessage());
        chatMessages.add(chatMsg);

        chatMessages.add(post(request));
    }

    public ChatMessage post(ChatRequest request){
        return restClient.post().uri("chat/completions")
                .body(request)
                .retrieve()
                .body(ChatMessage.class);
    }
}
