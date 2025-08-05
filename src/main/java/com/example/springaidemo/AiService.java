package com.example.springaidemo;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AiService {

    private final OpenAiChatModel chatModel;
    private final McpSyncClient sqliteMcp;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public MovieRecommendationResponse getMovieRecommendations(String promptMessage) {

        var outputConverter = new BeanOutputConverter<>(MovieRecommendationResponse.class);

        var jsonSchema = outputConverter.getJsonSchema();

        Prompt prompt = new Prompt(promptMessage,
                OpenAiChatOptions.builder()
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());

        ChatResponse response = chatModel.call(prompt);

        String content = response.getResult().getOutput().getText();

        return outputConverter.convert(content);
    }

    public MovieRecommendationResponse getAiResponseForPromptWithMcp(String promptMessage) {

        ChatClient client = ChatClient.builder(chatModel)
                .defaultToolCallbacks(
                        new SyncMcpToolCallbackProvider(sqliteMcp),
                        toolCallbackProvider
                )
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.1)
                        .build())
                .build();


        var outputConverter = new BeanOutputConverter<>(MovieRecommendationResponse.class);

        ChatResponse infoResponse = client.prompt()
                .user(u -> u.text("""
            Given the context, research and gather information for 5 movie recommendations.
            Use brave to search for the movies and get detailed descriptions.
            Use the database to find ratings for each movie.
            
            Context: {context}
            """)
                        .param("context", promptMessage))
                .call().chatResponse();

        return client.prompt()
                .user(u -> u.text("""
            Based on the following research, create 5 movie recommendations in the specified format:
            
            Research Data: {research}
            
            {format}
            """)
                        .param("research", infoResponse.getResult().getOutput().getText())
                        .param("format", outputConverter.getFormat()))
                .call()
                .entity(outputConverter);
    }

}
