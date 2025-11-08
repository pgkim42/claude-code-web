package com.example.bookmark.resolver;

import com.example.bookmark.dto.*;
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
    public BookmarkConnection bookmarksConnection(@Argument Integer first, @Argument String after) {
        return queryService.findWithCursor(first, after);
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
    public List<Bookmark> advancedSearch(@Argument BookmarkFilter filter) {
        return queryService.advancedSearch(filter);
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
    public Bookmark createBookmark(@Argument CreateBookmarkInput input) {
        return commandService.create(
                input.getTitle(),
                input.getUrl(),
                input.getDescription(),
                input.getCategoryId(),
                input.getTagIds(),
                input.getIsFavorite(),
                input.getRating(),
                input.getIsPublic()
        );
    }

    @MutationMapping
    public Bookmark updateBookmark(@Argument Long id, @Argument UpdateBookmarkInput input) {
        return commandService.update(
                id,
                input.getTitle(),
                input.getUrl(),
                input.getDescription(),
                input.getCategoryId(),
                input.getTagIds(),
                input.getIsFavorite(),
                input.getRating(),
                input.getIsPublic()
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
    public Bookmark createBookmarkFromUrl(@Argument CreateBookmarkFromUrlInput input) {
        Boolean fetchMetadata = Boolean.TRUE.equals(input.getFetchMetadata()) || input.getFetchMetadata() == null;

        return metadataService.createFromUrl(
                input.getUrl(),
                input.getCategoryId(),
                input.getTagIds(),
                input.getIsFavorite(),
                input.getIsPublic(),
                fetchMetadata
        );
    }

    @MutationMapping
    public Bookmark refreshMetadata(@Argument Long id) {
        return metadataService.refreshMetadata(id);
    }
}
