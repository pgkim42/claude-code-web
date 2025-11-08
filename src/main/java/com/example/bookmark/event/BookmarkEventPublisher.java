package com.example.bookmark.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Publisher for bookmark events.
 * Uses Reactor Sinks to emit events to GraphQL Subscriptions.
 *
 * Thread-safe and supports multiple subscribers.
 */
@Component
@Slf4j
public class BookmarkEventPublisher {

    // Multicast sink - multiple subscribers can receive the same events
    // Buffer size: 256 (bounded to prevent OutOfMemoryError)
    // Oldest events are dropped if buffer is full
    private final Sinks.Many<BookmarkEvent> sink = Sinks.many().multicast().onBackpressureBuffer(256);

    /**
     * Publish a bookmark event
     */
    public void publish(BookmarkEvent event) {
        log.info("Publishing bookmark event: {} for bookmark ID: {}",
                 event.getType(),
                 event.getBookmark() != null ? event.getBookmark().getId() : event.getBookmarkId());

        Sinks.EmitResult result = sink.tryEmitNext(event);

        if (result.isFailure()) {
            log.error("Failed to emit bookmark event: {}", result);
        }
    }

    /**
     * Get the Flux of bookmark events for subscriptions
     */
    public Flux<BookmarkEvent> getEvents() {
        return sink.asFlux();
    }

    /**
     * Get filtered Flux for specific event types
     */
    public Flux<BookmarkEvent> getEventsByType(BookmarkEvent.EventType type) {
        return sink.asFlux().filter(event -> event.getType() == type);
    }
}
