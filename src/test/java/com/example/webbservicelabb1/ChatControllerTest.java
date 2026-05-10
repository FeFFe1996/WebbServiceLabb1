package com.example.webbservicelabb1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    @DisplayName("GET / should return chatpage and populate model")
    void testGetChatPage() throws Exception {

        when(chatService.allMessages()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatpage"))
                .andExpect(model().attributeExists("chatMessages"))
                .andExpect(model().attributeExists("formRequest"));

        verify(chatService, times(1)).allMessages();
    }
    @Test
    @DisplayName("POST /api/v1/chat should redirect on success")
    void testSendRequest_Success() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        // Simulating form submission using params
                        .param("content", "Hello!")
                        .param("personality", "funny"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(chatService, times(1)).sendMessage(any(FormRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/chat should stay on page if validation fails")
    void testSendRequest_ValidationFailure() throws Exception {

        mockMvc.perform(post("/api/v1/chat")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("chatpage"))
                .andExpect(model().hasErrors());


        verify(chatService, never()).sendMessage(any(FormRequest.class));
    }
}