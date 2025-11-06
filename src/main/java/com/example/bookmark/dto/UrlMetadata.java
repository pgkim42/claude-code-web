package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMetadata {
    private String title;
    private String description;
    private String thumbnailUrl;
    private String faviconUrl;
    private String siteName;
    private String author;
    private LocalDateTime publishedDate;
    private String url;
}
