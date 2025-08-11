package com.example.springaidemo;

import com.fasterxml.jackson.annotation.JsonProperty;

record MovieRecommendationResponse(
        @JsonProperty(required = true, value = "recommendations") Recommendations recommendations,
        @JsonProperty(value = "summary") String summary) {

    record Recommendations(
            @JsonProperty(required = true, value = "items") RecommendationItem[] items) {

        record RecommendationItem(
                @JsonProperty(required = true, value = "title") String title,
                @JsonProperty(required = true, value = "description") String description,
                @JsonProperty(value = "rating") String rating,
                @JsonProperty(value = "cast") String[] cast,
                @JsonProperty(value = "genres") String[] genres,
                @JsonProperty(value = "reasonForRecommendation") String reasonForRecommendation) {
        }
    }
}