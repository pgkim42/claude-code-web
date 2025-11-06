package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkFilter {
    private String query;
    private Long categoryId;
    private String tagName;
    private Boolean isFavorite;
    private Integer minRating;
    private Boolean isPublic;
}
