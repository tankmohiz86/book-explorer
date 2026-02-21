package com.bookexplorer.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ActionItemsResponse {
    private String bookTitle;
    private String bookAuthor;
    private List<String> actionItems;
    private String model;
}
