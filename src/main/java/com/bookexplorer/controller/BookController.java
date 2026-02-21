package com.bookexplorer.controller;

import com.bookexplorer.dto.ActionItemsResponse;
import com.bookexplorer.dto.BookResult;
import com.bookexplorer.service.AiService;
import com.bookexplorer.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;
    private final AiService aiService;

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {

        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query parameter 'q' is required"));
        }

        try {
            List<BookResult> books = bookService.searchBooks(q.trim(), Math.min(limit, 20));
            return ResponseEntity.ok(Map.of(
                    "query", q,
                    "total", books.size(),
                    "books", books
            ));
        } catch (Exception e) {
            log.error("Search error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/action-items")
    public ResponseEntity<?> generateActionItems(@RequestBody ActionItemsRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Book title is required"));
        }

        try {
            ActionItemsResponse response = aiService.generateActionItems(
                    request.getTitle(),
                    request.getAuthor(),
                    request.getSummary(),
                    request.getSubjects()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Action items error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Book Explorer API",
                "version", "1.0.0"
        ));
    }

    // Inner record for request body
    @lombok.Data
    public static class ActionItemsRequest {
        private String title;
        private String author;
        private String summary;
        private List<String> subjects;
    }
}
