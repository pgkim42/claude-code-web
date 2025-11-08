package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Connection for Relay Cursor Pagination.
 * Represents a paginated list of bookmarks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkConnection {
    private List<BookmarkEdge> edges;
    private PageInfo pageInfo;
    private Integer totalCount;
}
