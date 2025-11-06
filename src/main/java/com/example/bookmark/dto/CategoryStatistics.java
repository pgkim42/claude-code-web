package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatistics {
    private Long categoryId;
    private String categoryName;
    private Long bookmarkCount;
}
