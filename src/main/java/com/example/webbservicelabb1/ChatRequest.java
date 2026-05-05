package com.example.webbservicelabb1;

import jakarta.validation.constraints.NotNull;

public class ChatRequest {
    private String personality;

    @NotNull
    private String message;

    private Long SessionId;
}
