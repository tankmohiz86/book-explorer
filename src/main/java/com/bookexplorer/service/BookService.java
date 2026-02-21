package com.bookexplorer.service;

import com.bookexplorer.dto.BookResult;
import com.bookexplorer.dto.OpenLibraryDoc;
import com.bookexplorer.dto.OpenLibraryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final WebClient openLibraryClient;

    public List<BookResult> searchBooks(String query, int limit) {
        log.info("Searching Open Library for: {}", query);

        try {
            OpenLibraryResponse response = openLibraryClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam("q", query)
                            .queryParam("limit", limit)
                            .queryParam("fields", "key,title,author_name,first_publish_year,cover_i,subject,first_sentence,number_of_pages_median,publisher,edition_count")
                            .build())
                    .retrieve()
                    .bodyToMono(OpenLibraryResponse.class)
                    .block();

            if (response == null || response.getDocs() == null) {
                return Collections.emptyList();
            }

            return response.getDocs().stream()
                    .map(this::toBookResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching Open Library: {}", e.getMessage());
            throw new RuntimeException("Failed to search books: " + e.getMessage());
        }
    }

    public BookResult getBookDetails(String workKey) {
        log.info("Fetching book details for key: {}", workKey);

        try {
            // workKey format: /works/OL12345W
            var workData = openLibraryClient.get()
                    .uri(workKey + ".json")
                    .retrieve()
                    .bodyToMono(java.util.Map.class)
                    .block();

            if (workData == null) return null;

            String title = (String) workData.get("title");
            String description = extractDescription(workData);

            return BookResult.builder()
                    .key(workKey)
                    .title(title)
                    .summary(description)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching book details: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractDescription(java.util.Map<String, Object> workData) {
        Object desc = workData.get("description");
        if (desc instanceof String) return (String) desc;
        if (desc instanceof java.util.Map) {
            return (String) ((java.util.Map<?, ?>) desc).get("value");
        }
        return null;
    }

    private BookResult toBookResult(OpenLibraryDoc doc) {
        String coverUrl = null;
        if (doc.getCoverId() != null) {
            coverUrl = "https://covers.openlibrary.org/b/id/" + doc.getCoverId() + "-L.jpg";
        }

        String author = doc.getAuthorName() != null && !doc.getAuthorName().isEmpty()
                ? doc.getAuthorName().get(0)
                : "Unknown Author";

        String summary = null;
        if (doc.getFirstSentence() != null && !doc.getFirstSentence().isEmpty()) {
            summary = doc.getFirstSentence().get(0);
        }

        List<String> subjects = doc.getSubject() != null
                ? doc.getSubject().stream().limit(5).collect(Collectors.toList())
                : Collections.emptyList();

        String publisher = doc.getPublisher() != null && !doc.getPublisher().isEmpty()
                ? doc.getPublisher().get(0)
                : null;

        return BookResult.builder()
                .key(doc.getKey())
                .title(doc.getTitle())
                .author(author)
                .year(doc.getFirstPublishYear())
                .coverUrl(coverUrl)
                .summary(summary)
                .subjects(subjects)
                .pages(doc.getNumberOfPagesMedian())
                .publisher(publisher)
                .editionCount(doc.getEditionCount())
                .build();
    }
}
