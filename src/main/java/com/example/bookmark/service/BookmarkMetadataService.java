package com.example.bookmark.service;

import com.example.bookmark.dto.UrlMetadata;
import com.example.bookmark.event.BookmarkEvent;
import com.example.bookmark.event.BookmarkEventPublisher;
import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.Category;
import com.example.bookmark.model.Tag;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for bookmark metadata operations.
 *
 * Single Responsibility: Handles URL metadata fetching and bookmark creation with metadata.
 * Benefits:
 * - Separates external service calls from core business logic
 * - Can mock/stub for testing
 * - Easy to add new metadata sources
 * - Retry/circuit breaker can be added here
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookmarkMetadataService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UrlMetadataService urlMetadataService;
    private final BookmarkEventPublisher eventPublisher;

    /**
     * Fetch metadata from URL without creating bookmark
     */
    @Transactional(readOnly = true)
    public UrlMetadata fetchMetadata(String url) {
        log.info("Fetching metadata for URL: {}", url);
        return urlMetadataService.fetchMetadata(url);
    }

    /**
     * Create bookmark from URL with auto-fetched metadata
     */
    public Bookmark createFromUrl(String url, Long categoryId, List<Long> tagIds,
                                  Boolean isFavorite, Boolean isPublic, Boolean fetchMetadata) {
        log.info("Creating bookmark from URL: {} (fetchMetadata: {})", url, fetchMetadata);

        Bookmark bookmark = new Bookmark();
        bookmark.setUrl(url);

        // Fetch and apply metadata if requested
        if (Boolean.TRUE.equals(fetchMetadata)) {
            UrlMetadata metadata = urlMetadataService.fetchMetadata(url);
            applyMetadata(bookmark, metadata);
        } else {
            bookmark.setTitle(url); // Fallback to URL as title
            bookmark.setMetadataFetched(false);
        }

        // Set category
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> ResourceNotFoundException.category(categoryId));
            bookmark.setCategory(category);
        }

        // Set tags
        if (tagIds != null && !tagIds.isEmpty()) {
            Set<Tag> tags = tagIds.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> ResourceNotFoundException.tag(tagId)))
                    .collect(Collectors.toSet());
            bookmark.setTags(tags);
        }

        // Set optional fields
        if (isFavorite != null) bookmark.setIsFavorite(isFavorite);
        if (isPublic != null) bookmark.setIsPublic(isPublic);

        Bookmark saved = bookmarkRepository.save(bookmark);
        log.info("Created bookmark from URL with id: {}", saved.getId());

        // Publish event for subscribers
        eventPublisher.publish(new BookmarkEvent(BookmarkEvent.EventType.CREATED, saved, null));

        return saved;
    }

    /**
     * Refresh metadata for existing bookmark
     */
    public Bookmark refreshMetadata(Long id) {
        log.info("Refreshing metadata for bookmark id: {}", id);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));

        UrlMetadata metadata = urlMetadataService.fetchMetadata(bookmark.getUrl());
        applyMetadata(bookmark, metadata);

        Bookmark updated = bookmarkRepository.save(bookmark);
        log.info("Refreshed metadata for bookmark id: {}", id);

        // Publish event for subscribers
        eventPublisher.publish(new BookmarkEvent(BookmarkEvent.EventType.UPDATED, updated, null));

        return updated;
    }

    /**
     * Apply metadata to bookmark
     */
    private void applyMetadata(Bookmark bookmark, UrlMetadata metadata) {
        if (metadata.getTitle() != null) {
            bookmark.setTitle(metadata.getTitle());
        } else {
            bookmark.setTitle(bookmark.getUrl()); // Fallback
        }

        bookmark.setDescription(metadata.getDescription());
        bookmark.setThumbnailUrl(metadata.getThumbnailUrl());
        bookmark.setFaviconUrl(metadata.getFaviconUrl());
        bookmark.setSiteName(metadata.getSiteName());
        bookmark.setAuthor(metadata.getAuthor());
        bookmark.setPublishedDate(metadata.getPublishedDate());
        bookmark.setMetadataFetched(true);
    }
}
