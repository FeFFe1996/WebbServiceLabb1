package com.example.webbservicelabb1;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService service){
        this.chatService = service;
    }

    @GetMapping("/")
    public String chat(Model model){
        model.addAttribute("chatMessages", chatService.allMessages());
        FormRequest request = new FormRequest();

        model.addAttribute("formRequest", request);
        return "chatpage";
    }

    @PostMapping("/api/v1/chat")
    public String sendRequest(@Valid @ModelAttribute("formRequest") FormRequest request,
                                   BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()){
            model.addAttribute("formRequest", request);
            return "chatpage";
        }

        chatService.sendMessage(request);
        return "redirect:/";
    }
}
