package com.example.webbservicelabb1.model;


import java.util.List;

public record ChatRequest(String model, List<ChatMessage> messages) {

}
