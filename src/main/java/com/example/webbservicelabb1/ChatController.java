package com.example.webbservicelabb1;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService service){
        this.chatService = service;
    }

    @GetMapping("/")
    public String chat(Model model){
        model.addAttribute("chatMessages", chatService.sessionMessages());
        model.addAttribute("formRequest", new ChatRequest());
        return "chatpage";
    }

    @PostMapping("/api/v1/chat")
    public String sendRequest(@Valid @ModelAttribute("formRequest") ChatRequest request,
                                   BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "chatpage";
        try {
            chatService.sendMsg(request);
        } catch (ResponseStatusException e){
            if (e.getStatusCode() == HttpStatus.CONFLICT){
                assert e.getReason() != null;
                bindingResult.rejectValue("message", e.getStatusCode().toString(), e.getReason());
                return "chatpage";
            }
            throw e;
        }
        return "redirect:chatpage";
    }
}
