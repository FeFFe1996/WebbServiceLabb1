package com.example.webbservicelabb1;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Service
public class ChatService {

    private final RestClient restClient;
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatService(RestClient restClient) {
        this.restClient = restClient;
    }

   public List<ChatMessage> allMessages(){
        return chatMessages;
   }

   public String sendMessage(FormRequest formRequest){
        ChatMessage message = new ChatMessage("user", formRequest.getContent());
        ChatMessage sysMsg = new ChatMessage("System", personality(formRequest.getPersonality()));
        ChatRequest request = new ChatRequest("meta-llama/llama-3.3-70b-instruct:free", List.of(sysMsg, message));
       int maxRetries = 3;
       int waitTimeMs = 2000;

       for (int i = 0; i < maxRetries; i++) {
           try {
               var response = getResponse(request);
               return response.choiceList().getFirst().message().content();
           } catch (RuntimeException e) {
               // Check if it's a 429 error and we have retries left
               if (e.getMessage().contains("429") && i < maxRetries - 1) {
                   try {
                       Thread.sleep(waitTimeMs);
                   } catch (InterruptedException ie) {
                       Thread.currentThread().interrupt();
                   }
                   waitTimeMs *= 2;
               } else {
                   throw e;
               }
           }
       }
       throw new RuntimeException("Failed to get response after multiple retries due to rate limits.");
   }

    private String personality(String personality) {
        String description = "";
        switch (personality) {
            case "pirate" ->  description = "You are a pirate of the seven seas, a real scallywag";
            case "salesman" -> description = "you are a top salesman of an it company that will do anything to sell your product";
            default -> description = "you are a regular chatbot, do your best";
        }

        return description;
    }


    public ChatResponse getResponse(ChatRequest chatRequest){
       return restClient.post()
               .uri("chat/completions")
               .contentType(MediaType.APPLICATION_JSON)
               .body(chatRequest)
               .retrieve()
               .onStatus(HttpStatusCode::isError, (req, res) -> {
                   throw new RuntimeException("Api Error " + res.getStatusCode());
               })
               .body(ChatResponse.class);
   }
}
