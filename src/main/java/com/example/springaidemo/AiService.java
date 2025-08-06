package com.example.springaidemo;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class AiService {

    private final OpenAiChatModel chatModel;
    private final OllamaChatModel ollamaChatModel;

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


    public String multiStep(String promptMessage) {

        ChatClient client = ChatClient.builder(ollamaChatModel)
                .defaultToolCallbacks(
                        new SyncMcpToolCallbackProvider(sqliteMcp),
                        toolCallbackProvider
                )
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.1)
                        .build())
                .build();

        ChatResponse movieTheatersResponse = client.prompt()
                .system("""
                        use the brave search engine to find information about cinemas
                        note that there is 1 second ratelimit on the brave search engine.
                        if you cannot find any cinemas, return an empty list.
            """)
                .user(u -> u.text("""
            Find cinemas in this location: { location }
            only give a list of cinema names and their websites if available.
            """)
                        .param("location", "utrecht"))
                .call().chatResponse();

        String content = movieTheatersResponse.getResult().getOutput().getText();
        log.info("cinema search result: {}", content);


        ChatResponse moviesInLocation = client.prompt()
                .tools(new WebTool())
                .system("""
                        Use the search from the getWebPage to find movies using the provided urls
                        From the result html find any mentions of movie titles
                        if you cannot find any movies, return an empty list.
                        Do not use the brave search engine for this.
                        """)
                .user(u -> u.text("""
            Find the movies playing this week in the mentioned cinemas in { location }            
            Cinemas: { cinemas }
            """)
                        .param("location", "utrecht")
                        .param("cinemas", content))
                .call().chatResponse();


        return moviesInLocation.getResult().getOutput().getText();
    }

}
