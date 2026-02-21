package com.bookexplorer.service;

import com.bookexplorer.dto.ActionItemsResponse;
import com.bookexplorer.dto.GroqRequest;
import com.bookexplorer.dto.GroqResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final WebClient groqClient;

    @Value("${app.groq.api-key:}")
    private String groqApiKey;

    @Value("${app.groq.model:openai/gpt-oss-120b}")
    private String groqModel;

    public ActionItemsResponse generateActionItems(String title, String author, String summary, List<String> subjects) {
        log.info("Generating action items for book: {} by {}", title, author);

        String subjectsText = subjects != null && !subjects.isEmpty()
                ? String.join(", ", subjects)
                : "general topics";

        String prompt = buildPrompt(title, author, summary, subjectsText);

        try {
            if (groqApiKey == null || groqApiKey.isBlank()) {
                log.warn("No Groq API key configured, returning mock response");
                return buildMockResponse(title, author);
            }

            GroqRequest request = GroqRequest.builder()
                    .model(groqModel)
                    .maxTokens(1024)
                    .temperature(0.7)
                    .messages(List.of(
                            GroqRequest.Message.builder()
                                    .role("system")
                                    .content("You are a knowledgeable book advisor. Generate practical, specific action items that readers can apply from books. Format your response as a numbered list with exactly 7 items. Each item should be concise (1-2 sentences) and directly actionable.")
                                    .build(),
                            GroqRequest.Message.builder()
                                    .role("user")
                                    .content(prompt)
                                    .build()
                    ))
                    .build();

            GroqResponse response = groqClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("Empty response from Groq API");
            }

            String content = response.getChoices().get(0).getMessage().getContent();
            List<String> actionItems = parseActionItems(content);

            return ActionItemsResponse.builder()
                    .bookTitle(title)
                    .bookAuthor(author)
                    .actionItems(actionItems)
                    .model(response.getModel() != null ? response.getModel() : groqModel)
                    .build();

        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage());
            throw new RuntimeException("Failed to generate action items: " + e.getMessage());
        }
    }

    private String buildPrompt(String title, String author, String summary, String subjects) {
        StringBuilder sb = new StringBuilder();
        sb.append("Book: \"").append(title).append("\" by ").append(author).append("\n");

        if (summary != null && !summary.isBlank()) {
            sb.append("Summary: ").append(summary).append("\n");
        }
        sb.append("Topics: ").append(subjects).append("\n\n");
        sb.append("Based on this book, generate exactly 7 specific, actionable steps a reader can implement in their life. ");
        sb.append("Make each action item practical and immediately applicable. Number them 1-7.");
        return sb.toString();
    }

    private List<String> parseActionItems(String content) {
        List<String> items = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+[.)\\s].*")) {
                // Remove the number and punctuation prefix
                String item = line.replaceFirst("^\\d+[.)\\s]+", "").trim();
                if (!item.isEmpty()) {
                    items.add(item);
                }
            }
        }

        // Fallback: if parsing fails, split by sentences
        if (items.isEmpty() || items.size() < 3) {
            items = Arrays.stream(content.split("\n"))
                    .filter(s -> !s.isBlank())
                    .limit(7)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        return items.stream().limit(7).collect(Collectors.toList());
    }

    private ActionItemsResponse buildMockResponse(String title, String author) {
        return ActionItemsResponse.builder()
                .bookTitle(title)
                .bookAuthor(author)
                .model("demo-mode")
                .actionItems(List.of(
                        "⚠️ Add your GROQ_API_KEY environment variable to enable AI-generated action items.",
                        "Visit https://console.groq.com to get your free API key.",
                        "Set GROQ_API_KEY in your .env file or docker-compose environment.",
                        "Groq offers free access to Llama 3, Mixtral, and Gemma models.",
                        "Once configured, this section will show 7 personalized action items from '" + title + "'.",
                        "The AI analyzes the book's themes, subjects, and content to generate relevant steps.",
                        "Restart the application after setting your API key to activate AI features."
                ))
                .build();
    }
}
