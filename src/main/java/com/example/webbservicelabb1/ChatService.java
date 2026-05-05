package com.example.webbservicelabb1;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    public List<ChatHistory> sessionMessages() {
        return List.of();
    }
}
