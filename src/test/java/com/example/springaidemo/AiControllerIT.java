package com.example.springaidemo;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "220s")
@ActiveProfiles("test")
public class AiControllerIT {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private OllamaChatModel chatModel;

    @Test
    void fetchMovieRecommendations() {
        RelevancyEvaluator relevancyEvaluator = RelevancyEvaluator.builder()
                .chatClientBuilder(ChatClient.builder(chatModel))
                .build();

        webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/ai/recommendations").queryParam("prompt", "some old action movies").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(jsonString -> {
                    EvaluationRequest evaluationRequest = new EvaluationRequest("Give movie recommendations for some old action movies as a JSON response", jsonString);
                    EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
                    assertThat(evaluationResponse.isPass()).isTrue();
                });
    }
}
