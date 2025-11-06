package com.example.bookmark.dto;

import lombok.Data;

import java.util.List;

/**
 * Input DTO for creating a new bookmark.
 * Maps to GraphQL CreateBookmarkInput type.
 */
@Data
public class CreateBookmarkInput {
    private String title;
    private String url;
    private String description;
    private Long categoryId;
    private List<Long> tagIds;
    private Boolean isFavorite;
    private Integer rating;
    private Boolean isPublic;
}
