package com.example.bookmark.service;

import com.example.bookmark.dto.*;
import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.User;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.security.BookmarkSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
    private final BookmarkSecurityService securityService;

    /**
     * Find all bookmarks
     * Returns only public bookmarks or user's own bookmarks
     */
    public List<Bookmark> findAll() {
        log.debug("Finding all bookmarks");
        List<Bookmark> allBookmarks = bookmarkRepository.findAll();
        return filterViewableBookmarks(allBookmarks);
    }

    /**
     * Find bookmark by ID
     * Only returns if public or owned by current user
     * @throws ResourceNotFoundException if bookmark not found
     */
    @PostAuthorize("@bookmarkSecurity.canView(returnObject)")
    public Bookmark findById(Long id) {
        log.debug("Finding bookmark by id: {}", id);
        return bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));
    }

    /**
     * Find bookmarks by category
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findByCategory(Long categoryId) {
        log.debug("Finding bookmarks by category: {}", categoryId);
        List<Bookmark> bookmarks = bookmarkRepository.findByCategoryId(categoryId);
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Simple search by title
     * Returns only viewable bookmarks
     */
    public List<Bookmark> searchByTitle(String query) {
        log.debug("Searching bookmarks by title: {}", query);
        List<Bookmark> bookmarks = bookmarkRepository.findByTitleContainingIgnoreCase(query);
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Advanced search with multiple filters
     * Returns only viewable bookmarks
     */
    public List<Bookmark> advancedSearch(BookmarkFilter filter) {
        log.debug("Advanced search with filter: {}", filter);
        List<Bookmark> bookmarks = bookmarkRepository.searchBookmarks(
                filter.getQuery(),
                filter.getCategoryId(),
                filter.getTagName(),
                filter.getIsFavorite(),
                filter.getMinRating()
        );
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Find favorite bookmarks
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findFavorites() {
        log.debug("Finding favorite bookmarks");
        List<Bookmark> favorites = bookmarkRepository.findByIsFavoriteTrue();
        return filterViewableBookmarks(favorites);
    }

    /**
     * Find bookmarks by tag name
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findByTag(String tagName) {
        log.debug("Finding bookmarks by tag: {}", tagName);
        List<Bookmark> bookmarks = bookmarkRepository.findByTagName(tagName);
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Find most visited bookmarks
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findMostVisited(Integer limit) {
        int pageSize = limit != null ? limit : 10;
        log.debug("Finding most visited bookmarks (limit: {})", pageSize);
        List<Bookmark> bookmarks = bookmarkRepository.findMostVisited(PageRequest.of(0, pageSize));
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Find recently visited bookmarks
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findRecentlyVisited(Integer limit) {
        int pageSize = limit != null ? limit : 10;
        log.debug("Finding recently visited bookmarks (limit: {})", pageSize);
        List<Bookmark> bookmarks = bookmarkRepository.findRecentlyVisited(PageRequest.of(0, pageSize));
        return filterViewableBookmarks(bookmarks);
    }

    /**
     * Find bookmarks with rating >= minRating
     * Returns only viewable bookmarks
     */
    public List<Bookmark> findTopRated(Integer minRating) {
        log.debug("Finding top rated bookmarks (minRating: {})", minRating);
        List<Bookmark> bookmarks = bookmarkRepository.findByRatingGreaterThanEqual(minRating);
        return filterViewableBookmarks(bookmarks);
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

        // Filter viewable bookmarks
        bookmarks = filterViewableBookmarks(bookmarks);

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

        // Get total count (for UI to show total items)
        int totalCount = (int) bookmarkRepository.count();

        return new BookmarkConnection(edges, pageInfo, totalCount);
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

    /**
     * Filter bookmarks to only include viewable ones
     * (public bookmarks or owned by current user)
     */
    private List<Bookmark> filterViewableBookmarks(List<Bookmark> bookmarks) {
        User currentUser = securityService.getCurrentUser();

        return bookmarks.stream()
                .filter(bookmark -> {
                    // Public bookmarks are viewable by everyone
                    if (Boolean.TRUE.equals(bookmark.getIsPublic())) {
                        return true;
                    }
                    // Private bookmarks only viewable by owner
                    if (currentUser != null && bookmark.getUser() != null) {
                        return bookmark.getUser().getId().equals(currentUser.getId());
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
