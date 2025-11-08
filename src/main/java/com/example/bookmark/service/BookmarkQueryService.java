package com.example.bookmark.service;

import com.example.bookmark.dto.*;
import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Query service for bookmark read operations.
 *
 * Follows CQRS pattern - handles only read operations.
 * Benefits:
 * - Clear separation of reads and writes
 * - Can be cached independently
 * - Easier to scale (read replicas)
 * - No side effects
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;

    /**
     * Find all bookmarks
     */
    public List<Bookmark> findAll() {
        log.debug("Finding all bookmarks");
        return bookmarkRepository.findAll();
    }

    /**
     * Find bookmark by ID
     * @throws ResourceNotFoundException if bookmark not found
     */
    public Bookmark findById(Long id) {
        log.debug("Finding bookmark by id: {}", id);
        return bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));
    }

    /**
     * Find bookmarks by category
     */
    public List<Bookmark> findByCategory(Long categoryId) {
        log.debug("Finding bookmarks by category: {}", categoryId);
        return bookmarkRepository.findByCategoryId(categoryId);
    }

    /**
     * Simple search by title
     */
    public List<Bookmark> searchByTitle(String query) {
        log.debug("Searching bookmarks by title: {}", query);
        return bookmarkRepository.findByTitleContainingIgnoreCase(query);
    }

    /**
     * Advanced search with multiple filters
     */
    public List<Bookmark> advancedSearch(BookmarkFilter filter) {
        log.debug("Advanced search with filter: {}", filter);
        return bookmarkRepository.searchBookmarks(
                filter.getQuery(),
                filter.getCategoryId(),
                filter.getTagName(),
                filter.getIsFavorite(),
                filter.getMinRating()
        );
    }

    /**
     * Find favorite bookmarks
     */
    public List<Bookmark> findFavorites() {
        log.debug("Finding favorite bookmarks");
        return bookmarkRepository.findByIsFavoriteTrue();
    }

    /**
     * Find bookmarks by tag name
     */
    public List<Bookmark> findByTag(String tagName) {
        log.debug("Finding bookmarks by tag: {}", tagName);
        return bookmarkRepository.findByTagName(tagName);
    }

    /**
     * Find most visited bookmarks
     */
    public List<Bookmark> findMostVisited(Integer limit) {
        int pageSize = limit != null ? limit : 10;
        log.debug("Finding most visited bookmarks (limit: {})", pageSize);
        return bookmarkRepository.findMostVisited(PageRequest.of(0, pageSize));
    }

    /**
     * Find recently visited bookmarks
     */
    public List<Bookmark> findRecentlyVisited(Integer limit) {
        int pageSize = limit != null ? limit : 10;
        log.debug("Finding recently visited bookmarks (limit: {})", pageSize);
        return bookmarkRepository.findRecentlyVisited(PageRequest.of(0, pageSize));
    }

    /**
     * Find bookmarks with rating >= minRating
     */
    public List<Bookmark> findTopRated(Integer minRating) {
        log.debug("Finding top rated bookmarks (minRating: {})", minRating);
        return bookmarkRepository.findByRatingGreaterThanEqual(minRating);
    }

    /**
     * Find bookmarks with cursor-based pagination
     * Implements Relay Cursor Connection specification
     *
     * @param first Number of items to fetch
     * @param after Cursor to start from (null for first page)
     * @return BookmarkConnection with edges and pageInfo
     */
    public BookmarkConnection findWithCursor(Integer first, String after) {
        int limit = first != null ? first : 20; // Default 20 items
        limit = Math.min(limit, 100); // Max 100 items per page

        log.debug("Finding bookmarks with cursor (first: {}, after: {})", limit, after);

        // Decode cursor to get the ID
        Long afterId = after != null ? decodeCursor(after) : 0L;

        // Fetch limit + 1 to check if there's a next page
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        List<Bookmark> bookmarks = bookmarkRepository.findByIdGreaterThanOrderByIdAsc(afterId, pageRequest);

        // Check if there's a next page
        boolean hasNextPage = bookmarks.size() > limit;
        if (hasNextPage) {
            bookmarks = bookmarks.subList(0, limit); // Remove the extra item
        }

        // Build edges
        List<BookmarkEdge> edges = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            String cursor = encodeCursor(bookmark.getId());
            edges.add(new BookmarkEdge(bookmark, cursor));
        }

        // Build pageInfo
        String startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();
        PageInfo pageInfo = new PageInfo(hasNextPage, after != null, startCursor, endCursor);

        // totalCount is null by default - computed lazily if requested by client
        // This avoids expensive count() query on every pagination request
        return new BookmarkConnection(edges, pageInfo, null);
    }

    /**
     * Get total count of bookmarks
     * Separate method for lazy computation
     */
    public int getTotalCount() {
        return (int) bookmarkRepository.count();
    }

    /**
     * Encode ID to cursor (Base64)
     */
    private String encodeCursor(Long id) {
        String cursorString = "bookmark:" + id;
        return Base64.getEncoder().encodeToString(cursorString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode cursor to ID
     */
    private Long decodeCursor(String cursor) {
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded.split(":")[1]);
        } catch (Exception e) {
            log.error("Failed to decode cursor: {}", cursor, e);
            throw new IllegalArgumentException("Invalid cursor format");
        }
    }
}
