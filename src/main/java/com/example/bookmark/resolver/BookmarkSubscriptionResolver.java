package com.example.bookmark.resolver;

import com.example.bookmark.event.BookmarkEvent;
import com.example.bookmark.event.BookmarkEventPublisher;
import com.example.bookmark.model.Bookmark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

/**
 * GraphQL Subscription resolver for real-time bookmark updates.
 *
 * Subscriptions use WebSocket to push updates to clients.
 * Clients can subscribe to specific event types.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BookmarkSubscriptionResolver {

    private final BookmarkEventPublisher eventPublisher;

    /**
     * Subscribe to all bookmark changes
     */
    @SubscriptionMapping
    public Flux<Bookmark> bookmarkChanged() {
        log.info("Client subscribed to bookmarkChanged");
        return eventPublisher.getEvents()
                .map(BookmarkEvent::getBookmark)
                .filter(bookmark -> bookmark != null); // Filter out DELETED events without bookmark
    }

    /**
     * Subscribe to bookmark creations only
     */
    @SubscriptionMapping
    public Flux<Bookmark> bookmarkCreated() {
        log.info("Client subscribed to bookmarkCreated");
        return eventPublisher.getEventsByType(BookmarkEvent.EventType.CREATED)
                .map(BookmarkEvent::getBookmark);
    }

    /**
     * Subscribe to bookmark updates only
     */
    @SubscriptionMapping
    public Flux<Bookmark> bookmarkUpdated() {
        log.info("Client subscribed to bookmarkUpdated");
        return eventPublisher.getEventsByType(BookmarkEvent.EventType.UPDATED)
                .map(BookmarkEvent::getBookmark);
    }

    /**
     * Subscribe to bookmark deletions only
     * Returns the ID of the deleted bookmark
     */
    @SubscriptionMapping
    public Flux<Long> bookmarkDeleted() {
        log.info("Client subscribed to bookmarkDeleted");
        return eventPublisher.getEventsByType(BookmarkEvent.EventType.DELETED)
                .map(event -> event.getBookmarkId() != null ?
                     event.getBookmarkId() :
                     event.getBookmark().getId());
    }
}
