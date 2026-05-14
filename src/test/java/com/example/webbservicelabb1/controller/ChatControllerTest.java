package com.example.webbservicelabb1.controller;

import com.example.webbservicelabb1.ChatService;
import com.example.webbservicelabb1.FormRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    @DisplayName("POST /api/v1/chat should return 200 OK on success")
    void testSendRequest_Success() throws Exception {
        FormRequest request = new FormRequest();
        request.setContent("Hello!");
        request.setPersonality("funny");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request))) // Sends JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(chatService, times(1)).sendMessage(any(FormRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/chat should return 400 Bad Request if validation fails")
    void testSendRequest_ValidationFailure() throws Exception {
        FormRequest invalidRequest = new FormRequest();

        invalidRequest.setContent("");
        invalidRequest.setPersonality("funny");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(chatService, never()).sendMessage(any(FormRequest.class));
    }
}