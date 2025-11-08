package com.example.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PageInfo for Relay Cursor Pagination.
 * Contains pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private String startCursor;
    private String endCursor;
}
