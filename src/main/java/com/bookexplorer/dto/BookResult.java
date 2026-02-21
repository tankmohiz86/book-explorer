package com.bookexplorer.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BookResult {
    private String key;
    private String title;
    private String author;
    private Integer year;
    private String coverUrl;
    private String summary;
    private List<String> subjects;
    private Integer pages;
    private String publisher;
    private Integer editionCount;
}
