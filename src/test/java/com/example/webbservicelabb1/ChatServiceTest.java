package com.example.webbservicelabb1;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class ChatServiceTest {

    private ChatService chatService;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(wmRuntimeInfo.getHttpBaseUrl())
                .build();

        this.chatService = new ChatService(restClient);
    }

    @Test
    @DisplayName("Should return response successfully on 200 OK")
    void sendMessage_Success() {
        FormRequest formRequest = new FormRequest();
        formRequest.setPersonality("helpful");
        formRequest.setContent("Hello!");
        String successPayload = """
            {
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "Hi there! Ready to assist."
                  }
                }
              ]
            }
            """;

        stubFor(post(urlEqualTo("/chat/completions"))
                .willReturn(okJson(successPayload)));

        String result = chatService.sendMessage(formRequest);

        assertEquals("Hi there! Ready to assist.", result);
        verify(1, postRequestedFor(urlEqualTo("/chat/completions")));
    }

    @Test
    @DisplayName("Should retry when hitting a 429 rate limit and eventually succeed")
    void sendMessage_RateLimit_RetriesAndSucceeds() {
        FormRequest formRequest = new FormRequest();
        formRequest.setPersonality("funny");
        formRequest.setContent("tell me a joke");

        String urlPath = "/chat/completions";

        String successJson = """
            {
              "choices": [ { "message": { "content": "I am the assistant response" } } ]
            }
            """;

        stubFor(post(urlEqualTo(urlPath))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(status(429)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Retry-After", "1")
                        .withBody("{\"error\": \"Rate limit exceeded\"}"))
                .willSetStateTo("SucceedOnRetry"));


        stubFor(post(urlEqualTo(urlPath))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("SucceedOnRetry")
                .willReturn(okJson(successJson)));


        String result = chatService.sendMessage(formRequest);


        assertEquals("I am the assistant response", result);
        verify(2, postRequestedFor(urlEqualTo(urlPath)));
        }
}

