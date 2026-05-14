package com.example.webbservicelabb1.controller;
import com.example.webbservicelabb1.ChatService;
import com.example.webbservicelabb1.FormRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService service){
        this.chatService = service;
    }

    @PostMapping("/api/v1/chat")
    public ResponseEntity<?> sendRequest(@Valid @RequestBody FormRequest request,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        chatService.sendMessage(request);
        return ResponseEntity.ok(Collections.singletonMap("status", "success"));
    }
}
