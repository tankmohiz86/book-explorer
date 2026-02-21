package com.bookexplorer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GroqRequest {
    private String model;
    private List<Message> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;

    private double temperature;

    @Data
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}
