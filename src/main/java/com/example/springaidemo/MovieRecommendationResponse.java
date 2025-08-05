package com.example.springaidemo;

import com.fasterxml.jackson.annotation.JsonProperty;

record MovieRecommendationResponse(
        @JsonProperty(required = true, value = "recommendations") Recommendations recommendations,
        @JsonProperty(required = true, value = "summary") String summary,
        @JsonProperty(required = false, value = "metadata") Metadata metadata) {

    record Recommendations(
            @JsonProperty(required = true, value = "items") RecommendationItem[] items) {

        record RecommendationItem(
                @JsonProperty(required = true, value = "title") String title,
                @JsonProperty(required = true, value = "description") String description,
                @JsonProperty(required = false, value = "category") String category,
                @JsonProperty(required = false, value = "rating") String rating,
                @JsonProperty(required = false, value = "duration") String duration,
                @JsonProperty(required = false, value = "releaseDate") String releaseDate,
                @JsonProperty(required = false, value = "matchScore") Integer matchScore,
                @JsonProperty(required = false, value = "cast") String[] cast,
                @JsonProperty(required = false, value = "genres") String[] genres,
                @JsonProperty(required = false, value = "reasonForRecommendation") String reasonForRecommendation) {
        }
    }

    record Metadata(
            @JsonProperty(required = false, value = "queryParameters") QueryParameters queryParameters,
            @JsonProperty(required = false, value = "confidence") Float confidence,
            @JsonProperty(required = false, value = "totalResults") Integer totalResults,
            @JsonProperty(required = false, value = "timestamp") String timestamp) {

        record QueryParameters(
                @JsonProperty(required = false, value = "genres") String[] genres,
                @JsonProperty(required = false, value = "actors") String[] actors,
                @JsonProperty(required = false, value = "directors") String[] directors,
                @JsonProperty(required = false, value = "releaseYearMin") Integer releaseYearMin,
                @JsonProperty(required = false, value = "releaseYearMax") Integer releaseYearMax,
                @JsonProperty(required = false, value = "ratingMin") String ratingMin,
                @JsonProperty(required = false, value = "country") String country) {
        }
    }
}