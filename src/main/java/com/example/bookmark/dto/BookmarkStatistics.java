package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkStatistics {
    private Long totalBookmarks;
    private Long totalFavorites;
    private Long totalVisits;
    private Double averageRating;
    private Long totalCategories;
    private Long totalTags;
}
