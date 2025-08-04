package com.example.springaidemo;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/prompt")
    public PromptResponse getAiResponseForPrompt(@RequestParam(name = "prompt") String prompt) {
        return aiService.getAiResponseForPrompt(prompt);
    }

}
