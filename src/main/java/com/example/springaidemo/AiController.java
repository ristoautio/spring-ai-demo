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

    @GetMapping("/recommendations")
    public MovieRecommendationResponse getRecommendationsByPrompt(@RequestParam(name = "prompt") String prompt) {
        return aiService.getMovieRecommendations(prompt);
    }

    @GetMapping("/recommendations-mcp")
    public MovieRecommendationResponse getAiResponseForPrompt(@RequestParam(name = "prompt") String prompt) {
        return aiService.getAiResponseForPromptWithMcp(prompt);
    }

    @GetMapping("/cinema")
    public String getAiResponseForPromptWithAdvisor(@RequestParam(name = "prompt") String prompt) {
        return aiService.multiStep(prompt);
    }

}
