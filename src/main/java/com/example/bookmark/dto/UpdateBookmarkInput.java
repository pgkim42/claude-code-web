package com.example.bookmark.dto;

import lombok.Data;

import java.util.List;

/**
 * Input DTO for updating an existing bookmark.
 * Maps to GraphQL UpdateBookmarkInput type.
 * All fields are optional.
 */
@Data
public class UpdateBookmarkInput {
    private String title;
    private String url;
    private String description;
    private Long categoryId;
    private List<Long> tagIds;
    private Boolean isFavorite;
    private Integer rating;
    private Boolean isPublic;
}
