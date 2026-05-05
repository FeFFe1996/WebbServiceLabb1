package com.example.webbservicelabb1;

import jakarta.validation.constraints.NotNull;

public class ChatRequest {
    private String personality;

    @NotNull
    private String message;

    private Long SessionId;

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSessionId() {
        return SessionId;
    }

    public void setSessionId(Long sessionId) {
        SessionId = sessionId;
    }
}
