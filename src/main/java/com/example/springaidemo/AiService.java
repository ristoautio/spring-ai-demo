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
    private final McpSyncClient mcpSyncClient;

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

    public String getAiResponseForPromptWithMcp(String promptMessage) {
        ChatClient client = ChatClient.builder(chatModel)
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        ChatResponse response = client.prompt(Prompt.builder().content(promptMessage).build()).call().chatResponse();

        String content = response.getResult().getOutput().getText();

        return content;
    }

}
