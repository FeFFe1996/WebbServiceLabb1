package com.example.webbservicelabb1.controller;

import com.example.webbservicelabb1.ChatService;
import com.example.webbservicelabb1.FormRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ChatService chatService;
    public HomeController(ChatService service){
        this.chatService = service;
    }

    @GetMapping("/")
    public String chat(Model model){
        model.addAttribute("chatMessages", chatService.allMessages());
        FormRequest request = new FormRequest();

        model.addAttribute("formRequest", request);
        return "chatpage";
    }
}
