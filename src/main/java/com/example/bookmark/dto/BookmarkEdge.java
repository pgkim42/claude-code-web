package com.example.bookmark.dto;

import com.example.bookmark.model.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Edge for Relay Cursor Pagination.
 * Represents a bookmark with its cursor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEdge {
    private Bookmark node;
    private String cursor;
}
