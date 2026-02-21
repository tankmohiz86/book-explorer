package com.bookexplorer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryDoc {

    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author_name")
    private List<String> authorName;

    @JsonProperty("first_publish_year")
    private Integer firstPublishYear;

    @JsonProperty("cover_i")
    private Long coverId;

    @JsonProperty("subject")
    private List<String> subject;

    @JsonProperty("first_sentence")
    private List<String> firstSentence;

    @JsonProperty("number_of_pages_median")
    private Integer numberOfPagesMedian;

    @JsonProperty("publisher")
    private List<String> publisher;

    @JsonProperty("language")
    private List<String> language;

    @JsonProperty("isbn")
    private List<String> isbn;

    @JsonProperty("edition_count")
    private Integer editionCount;
}
