package com.example.springaidemo;

import com.fasterxml.jackson.annotation.JsonProperty;

record PromptResponse(
        @JsonProperty(required = true, value = "content") Content content,
        @JsonProperty(required = true, value = "summary") String summary,
        @JsonProperty(required = false, value = "metadata") Metadata metadata) {

    record Content(
            @JsonProperty(required = true, value = "sections") Section[] sections) {

        record Section(
                @JsonProperty(required = true, value = "text") String text,
                @JsonProperty(required = false, value = "type") String type,
                @JsonProperty(required = false, value = "references") Reference[] references) {
        }

        record Reference(
                @JsonProperty(required = true, value = "source") String source,
                @JsonProperty(required = false, value = "url") String url) {
        }
    }

    record Metadata(
            @JsonProperty(required = false, value = "confidence") Float confidence,
            @JsonProperty(required = false, value = "model") String model,
            @JsonProperty(required = false, value = "timestamp") String timestamp) {
    }
}