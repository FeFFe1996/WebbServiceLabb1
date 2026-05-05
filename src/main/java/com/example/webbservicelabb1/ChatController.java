package com.example.webbservicelabb1;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService service){
        this.chatService = service;
    }

    @GetMapping("/")
    public String chat(Model model){
        model.addAttribute("chatMessages", chatService.sessionMessages());
        return "chatpage";
    }
}
