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
        ChatMessage sysMsg = new ChatMessage("system", personality(formRequest.getPersonality()));
        ChatRequest request = new ChatRequest("meta-llama/llama-3.3-70b-instruct:free", List.of(sysMsg, message));
       int maxRetries = 3;
       int waitTimeMs = 2000;

       for (int i = 0; i < maxRetries; i++) {
           try {
               var response = getResponse(request);
               if(response == null || response.choices() == null ||
                       response.choices().isEmpty() ||
                       response.choices().get(0).message() == null ||
                       response.choices().get(0).message().content() == null){
                   throw new IllegalStateException("Chat Api didnt return no assistant message");
               }
               var assistantContent = response.choices().get(0).message().content();
               chatMessages.add(message);
               chatMessages.add(new ChatMessage("assistant", assistantContent));
               return assistantContent;
           } catch (RuntimeException e) {
               boolean isRateLimit = e.getMessage().contains("429");
               boolean isServerError = e.getMessage().contains("500");
               if ((isRateLimit || isServerError) && i < maxRetries - 1) {
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
        if (personality == null || personality.isBlank()) {
            return "you are a regular chatbot, do your best";
        }
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
               .header("HTTP-Referer", "https://myapp.example")
               .header("X-Title", "WebbServiceLabb1")
               .body(chatRequest)
               .retrieve()
               .onStatus(HttpStatusCode::isError, (req, res) -> {
                   if (res.getStatusCode().value() == 429) {
                       // Extract Retry-After header (value in seconds, default to 5 seconds if missing)
                       String retryAfter = res.getHeaders().getFirst("Retry-After");
                       long waitSeconds = retryAfter != null ? Long.parseLong(retryAfter) : 5;
                       throw new RuntimeException("429:" + waitSeconds);
                   }
                   throw new RuntimeException("API Error " + res.getStatusCode());
               })
               .body(ChatResponse.class);
   }
}
