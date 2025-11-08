package com.example.bookmark.event;

import com.example.bookmark.model.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event representing a change to a bookmark.
 * Used for GraphQL Subscriptions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEvent {

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }

    private EventType type;
    private Bookmark bookmark;
    private Long bookmarkId; // For DELETED events where bookmark might not exist
}
