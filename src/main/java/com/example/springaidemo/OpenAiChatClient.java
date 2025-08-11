package com.example.springaidemo;


import io.modelcontextprotocol.client.McpSyncClient;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@AllArgsConstructor
public class OpenAiChatClient {

    private final OpenAiChatModel chatModel;
    private final McpSyncClient sqliteMcp;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(
                        new SyncMcpToolCallbackProvider(sqliteMcp),
                        toolCallbackProvider
                )
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.1)
                        .build())
                .build();
    }
}
