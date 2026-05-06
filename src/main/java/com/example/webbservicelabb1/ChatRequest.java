package com.example.webbservicelabb1;


import java.util.List;

public record ChatRequest(String model, List<ChatMessage> messages) {

}
