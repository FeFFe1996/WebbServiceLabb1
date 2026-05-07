package com.example.webbservicelabb1;

import com.example.webbservicelabb1.exception.ApiException;
import com.example.webbservicelabb1.exception.RateLimitExceededException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Service
public class ChatService {
    private static final long MAX_RETRY_SLEEP_MS = 10_000L;
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
        ChatRequest request = new ChatRequest("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning:free", List.of(sysMsg, message));
       int maxRetries = 5;
       int waitTimeMs = 2000;

       for (int i = 0; i < maxRetries; i++) {
           try {
               var response = getResponse(request);

               if (response == null || response.choices() == null || response.choices().isEmpty() ||
                       response.choices().get(0).message() == null ||
                       response.choices().get(0).message().content() == null) {
                   throw new IllegalStateException("Chat API didn't return an assistant message");
               }

               var assistantContent = response.choices().get(0).message().content();
               chatMessages.add(message);
               chatMessages.add(new ChatMessage("assistant", assistantContent));
               return assistantContent;

           } catch (RateLimitExceededException e) {
               long sleep = (e.getWaitSeconds() > 0) ? e.getWaitSeconds() * 1000L : waitTimeMs;
               handleRetry(i, maxRetries, sleep, e);
               waitTimeMs *= 2;
           } catch (ApiException e) {
               if (isTransientError(e.getStatusCode())) {
                   handleRetry(i, maxRetries, waitTimeMs, e);
                   waitTimeMs *= 2;
               } else {

                   throw e;
               }
           }
       }
       throw new ApiException(503, "Failed to get response after " + maxRetries + " retries.");
   }

    private boolean isTransientError(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    private void handleRetry(int currentAttempt, int maxRetries, long sleepMs, RuntimeException originalException) {
        if (currentAttempt >= maxRetries - 1) {
            throw originalException;
        }
        try {
            Thread.sleep(Math.clamp(sleepMs, 0L, MAX_RETRY_SLEEP_MS));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", ie);
        }
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
                   String body = new String(res.getBody().readAllBytes());
                   System.out.println("error: " + body);
                   int status = res.getStatusCode().value();
                   if (status == 429) {
                       String retryAfter = res.getHeaders().getFirst("Retry-After");
                       long waitSeconds = 5;
                       if (retryAfter != null){
                           try {
                               waitSeconds = Math.max(0L, Long.parseLong(retryAfter.trim()));
                           } catch (NumberFormatException ignored){
                           }
                       }
                       throw new RateLimitExceededException(waitSeconds, "Rate limit exceeded");
                   }
                   throw new ApiException(status, "API Error: " + res.getStatusText());
               })
               .body(ChatResponse.class);
   }
}
