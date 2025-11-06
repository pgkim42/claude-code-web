package com.example.bookmark.dto;

import lombok.Data;

import java.util.List;

/**
 * Input DTO for creating a bookmark from URL with automatic metadata fetching.
 * Maps to GraphQL CreateBookmarkFromUrlInput type.
 */
@Data
public class CreateBookmarkFromUrlInput {
    private String url;
    private Long categoryId;
    private List<Long> tagIds;
    private Boolean isFavorite;
    private Boolean isPublic;
    private Boolean fetchMetadata;
}
