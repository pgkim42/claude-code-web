package com.example.bookmark.resolver;

import com.example.bookmark.dto.BookmarkFilter;
import com.example.bookmark.dto.BookmarkStatistics;
import com.example.bookmark.dto.CategoryStatistics;
import com.example.bookmark.dto.UrlMetadata;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkCommandService;
import com.example.bookmark.service.BookmarkMetadataService;
import com.example.bookmark.service.BookmarkQueryService;
import com.example.bookmark.service.BookmarkStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * GraphQL resolver for bookmark operations.
 *
 * Follows thin controller pattern:
 * - Minimal logic, delegates to services
 * - Converts GraphQL inputs to service calls
 * - Returns domain models (for now - will use DTOs later)
 *
 * Dependencies: 4 specialized services (SRP)
 */
@Controller
@RequiredArgsConstructor
public class BookmarkResolver {

    private final BookmarkQueryService queryService;
    private final BookmarkCommandService commandService;
    private final BookmarkStatisticsService statisticsService;
    private final BookmarkMetadataService metadataService;

    // ========== Query Operations ==========

    @QueryMapping
    public List<Bookmark> bookmarks() {
        return queryService.findAll();
    }

    @QueryMapping
    public Bookmark bookmark(@Argument Long id) {
        return queryService.findById(id);
    }

    @QueryMapping
    public List<Bookmark> bookmarksByCategory(@Argument Long categoryId) {
        return queryService.findByCategory(categoryId);
    }

    @QueryMapping
    public List<Bookmark> searchBookmarks(@Argument String query) {
        return queryService.searchByTitle(query);
    }

    @QueryMapping
    public List<Bookmark> advancedSearch(@Argument Map<String, Object> filter) {
        BookmarkFilter bookmarkFilter = mapToBookmarkFilter(filter);
        return queryService.advancedSearch(bookmarkFilter);
    }

    @QueryMapping
    public List<Bookmark> favoriteBookmarks() {
        return queryService.findFavorites();
    }

    @QueryMapping
    public List<Bookmark> bookmarksByTag(@Argument String tagName) {
        return queryService.findByTag(tagName);
    }

    @QueryMapping
    public List<Bookmark> mostVisitedBookmarks(@Argument Integer limit) {
        return queryService.findMostVisited(limit);
    }

    @QueryMapping
    public List<Bookmark> recentlyVisitedBookmarks(@Argument Integer limit) {
        return queryService.findRecentlyVisited(limit);
    }

    @QueryMapping
    public List<Bookmark> topRatedBookmarks(@Argument Integer minRating) {
        return queryService.findTopRated(minRating);
    }

    // ========== Statistics Operations ==========

    @QueryMapping
    public BookmarkStatistics bookmarkStatistics() {
        return statisticsService.getOverallStatistics();
    }

    @QueryMapping
    public List<CategoryStatistics> categoryStatistics() {
        return statisticsService.getCategoryStatistics();
    }

    // ========== Metadata Operations ==========

    @QueryMapping
    public UrlMetadata fetchUrlMetadata(@Argument String url) {
        return metadataService.fetchMetadata(url);
    }

    // ========== Mutation Operations ==========

    @MutationMapping
    public Bookmark createBookmark(@Argument Map<String, Object> input) {
        return commandService.create(
                (String) input.get("title"),
                (String) input.get("url"),
                (String) input.get("description"),
                parseLong(input.get("categoryId")),
                parseLongList(input.get("tagIds")),
                (Boolean) input.get("isFavorite"),
                parseInteger(input.get("rating")),
                (Boolean) input.get("isPublic")
        );
    }

    @MutationMapping
    public Bookmark updateBookmark(@Argument Long id, @Argument Map<String, Object> input) {
        return commandService.update(
                id,
                (String) input.get("title"),
                (String) input.get("url"),
                (String) input.get("description"),
                parseLong(input.get("categoryId")),
                parseLongList(input.get("tagIds")),
                (Boolean) input.get("isFavorite"),
                parseInteger(input.get("rating")),
                (Boolean) input.get("isPublic")
        );
    }

    @MutationMapping
    public Boolean deleteBookmark(@Argument Long id) {
        return commandService.delete(id);
    }

    @MutationMapping
    public Bookmark recordVisit(@Argument Long id) {
        return commandService.recordVisit(id);
    }

    @MutationMapping
    public Bookmark toggleFavorite(@Argument Long id) {
        return commandService.toggleFavorite(id);
    }

    @MutationMapping
    public Bookmark setRating(@Argument Long id, @Argument Integer rating) {
        return commandService.setRating(id, rating);
    }

    @MutationMapping
    public Bookmark addTagToBookmark(@Argument Long bookmarkId, @Argument Long tagId) {
        return commandService.addTag(bookmarkId, tagId);
    }

    @MutationMapping
    public Bookmark removeTagFromBookmark(@Argument Long bookmarkId, @Argument Long tagId) {
        return commandService.removeTag(bookmarkId, tagId);
    }

    // ========== Metadata Mutation Operations ==========

    @MutationMapping
    public Bookmark createBookmarkFromUrl(@Argument Map<String, Object> input) {
        Boolean fetchMetadata = input.get("fetchMetadata") != null ?
                (Boolean) input.get("fetchMetadata") : true; // Default to true

        return metadataService.createFromUrl(
                (String) input.get("url"),
                parseLong(input.get("categoryId")),
                parseLongList(input.get("tagIds")),
                (Boolean) input.get("isFavorite"),
                (Boolean) input.get("isPublic"),
                fetchMetadata
        );
    }

    @MutationMapping
    public Bookmark refreshMetadata(@Argument Long id) {
        return metadataService.refreshMetadata(id);
    }

    // ========== Helper Methods ==========

    private BookmarkFilter mapToBookmarkFilter(Map<String, Object> filterMap) {
        BookmarkFilter filter = new BookmarkFilter();
        filter.setQuery((String) filterMap.get("query"));
        filter.setCategoryId(parseLong(filterMap.get("categoryId")));
        filter.setTagName((String) filterMap.get("tagName"));
        filter.setIsFavorite((Boolean) filterMap.get("isFavorite"));
        filter.setMinRating(parseInteger(filterMap.get("minRating")));
        filter.setIsPublic((Boolean) filterMap.get("isPublic"));
        return filter;
    }

    private Long parseLong(Object value) {
        return value != null ? Long.parseLong(value.toString()) : null;
    }

    private Integer parseInteger(Object value) {
        return value != null ? Integer.parseInt(value.toString()) : null;
    }

    private List<Long> parseLongList(Object value) {
        if (value == null) return null;
        return ((List<?>) value).stream()
                .map(id -> Long.parseLong(id.toString()))
                .toList();
    }
}
