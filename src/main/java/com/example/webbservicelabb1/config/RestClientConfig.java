package com.example.webbservicelabb1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

        @Value("${API_KEY}")
        private String apiKey;

        @Bean
        public RestClient openRouterClient() {
            return RestClient.builder()
                    .baseUrl("https://openrouter.ai/api/v1/")
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("HTTP-Referer", "https://myapp.example")
                    .defaultHeader("X-Title", "WebbServiceLabb1")
                    .defaultHeader("X-OpenRouter-Cache", "true")
                    .build();
        }
}

