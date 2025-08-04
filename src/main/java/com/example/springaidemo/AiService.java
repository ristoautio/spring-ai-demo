package com.example.springaidemo;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AiService {

    private final OpenAiChatModel chatModel;

    public PromptResponse getAiResponseForPrompt(String promptMessage) {

        var outputConverter = new BeanOutputConverter<>(PromptResponse.class);

        var jsonSchema = outputConverter.getJsonSchema();

        Prompt prompt = new Prompt(promptMessage,
                OpenAiChatOptions.builder()
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());

        ChatResponse response = chatModel.call(prompt);

        String content = response.getResult().getOutput().getText();

        return outputConverter.convert(content);
    }
}
