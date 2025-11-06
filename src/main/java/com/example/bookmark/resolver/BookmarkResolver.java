package com.example.bookmark.resolver;

import com.example.bookmark.dto.BookmarkFilter;
import com.example.bookmark.dto.BookmarkStatistics;
import com.example.bookmark.dto.CategoryStatistics;
import com.example.bookmark.dto.UrlMetadata;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BookmarkResolver {

    private final BookmarkService bookmarkService;

    @QueryMapping
    public List<Bookmark> bookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @QueryMapping
    public Bookmark bookmark(@Argument Long id) {
        return bookmarkService.getBookmarkById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));
    }

    @QueryMapping
    public List<Bookmark> bookmarksByCategory(@Argument Long categoryId) {
        return bookmarkService.getBookmarksByCategory(categoryId);
    }

    @QueryMapping
    public List<Bookmark> searchBookmarks(@Argument String query) {
        return bookmarkService.searchBookmarks(query);
    }

    @QueryMapping
    public List<Bookmark> advancedSearch(@Argument Map<String, Object> filter) {
        BookmarkFilter bookmarkFilter = mapToBookmarkFilter(filter);
        return bookmarkService.advancedSearch(bookmarkFilter);
    }

    @QueryMapping
    public List<Bookmark> favoriteBookmarks() {
        return bookmarkService.getFavoriteBookmarks();
    }

    @QueryMapping
    public List<Bookmark> bookmarksByTag(@Argument String tagName) {
        return bookmarkService.getBookmarksByTag(tagName);
    }

    @QueryMapping
    public List<Bookmark> mostVisitedBookmarks(@Argument Integer limit) {
        return bookmarkService.getMostVisitedBookmarks(limit);
    }

    @QueryMapping
    public List<Bookmark> recentlyVisitedBookmarks(@Argument Integer limit) {
        return bookmarkService.getRecentlyVisitedBookmarks(limit);
    }

    @QueryMapping
    public List<Bookmark> topRatedBookmarks(@Argument Integer minRating) {
        return bookmarkService.getTopRatedBookmarks(minRating);
    }

    @QueryMapping
    public BookmarkStatistics bookmarkStatistics() {
        return bookmarkService.getBookmarkStatistics();
    }

    @QueryMapping
    public List<CategoryStatistics> categoryStatistics() {
        return bookmarkService.getCategoryStatistics();
    }

    @MutationMapping
    public Bookmark createBookmark(@Argument Map<String, Object> input) {
        String title = (String) input.get("title");
        String url = (String) input.get("url");
        String description = (String) input.get("description");
        Long categoryId = input.get("categoryId") != null ?
                Long.parseLong(input.get("categoryId").toString()) : null;
        List<Long> tagIds = input.get("tagIds") != null ?
                ((List<?>) input.get("tagIds")).stream()
                        .map(id -> Long.parseLong(id.toString()))
                        .toList() : null;
        Boolean isFavorite = (Boolean) input.get("isFavorite");
        Integer rating = input.get("rating") != null ?
                Integer.parseInt(input.get("rating").toString()) : null;
        Boolean isPublic = (Boolean) input.get("isPublic");

        return bookmarkService.createBookmark(title, url, description, categoryId,
                tagIds, isFavorite, rating, isPublic);
    }

    @MutationMapping
    public Bookmark updateBookmark(@Argument Long id, @Argument Map<String, Object> input) {
        String title = (String) input.get("title");
        String url = (String) input.get("url");
        String description = (String) input.get("description");
        Long categoryId = input.get("categoryId") != null ?
                Long.parseLong(input.get("categoryId").toString()) : null;
        List<Long> tagIds = input.get("tagIds") != null ?
                ((List<?>) input.get("tagIds")).stream()
                        .map(tagId -> Long.parseLong(tagId.toString()))
                        .toList() : null;
        Boolean isFavorite = (Boolean) input.get("isFavorite");
        Integer rating = input.get("rating") != null ?
                Integer.parseInt(input.get("rating").toString()) : null;
        Boolean isPublic = (Boolean) input.get("isPublic");

        return bookmarkService.updateBookmark(id, title, url, description, categoryId,
                tagIds, isFavorite, rating, isPublic);
    }

    @MutationMapping
    public Boolean deleteBookmark(@Argument Long id) {
        return bookmarkService.deleteBookmark(id);
    }

    @MutationMapping
    public Bookmark recordVisit(@Argument Long id) {
        return bookmarkService.recordVisit(id);
    }

    @MutationMapping
    public Bookmark toggleFavorite(@Argument Long id) {
        return bookmarkService.toggleFavorite(id);
    }

    @MutationMapping
    public Bookmark setRating(@Argument Long id, @Argument Integer rating) {
        return bookmarkService.setRating(id, rating);
    }

    @MutationMapping
    public Bookmark addTagToBookmark(@Argument Long bookmarkId, @Argument Long tagId) {
        return bookmarkService.addTagToBookmark(bookmarkId, tagId);
    }

    @MutationMapping
    public Bookmark removeTagFromBookmark(@Argument Long bookmarkId, @Argument Long tagId) {
        return bookmarkService.removeTagFromBookmark(bookmarkId, tagId);
    }

    // URL Metadata Operations

    @QueryMapping
    public UrlMetadata fetchUrlMetadata(@Argument String url) {
        return bookmarkService.fetchUrlMetadata(url);
    }

    @MutationMapping
    public Bookmark createBookmarkFromUrl(@Argument Map<String, Object> input) {
        String url = (String) input.get("url");
        Long categoryId = input.get("categoryId") != null ?
                Long.parseLong(input.get("categoryId").toString()) : null;
        List<Long> tagIds = input.get("tagIds") != null ?
                ((List<?>) input.get("tagIds")).stream()
                        .map(id -> Long.parseLong(id.toString()))
                        .toList() : null;
        Boolean isFavorite = (Boolean) input.get("isFavorite");
        Boolean isPublic = (Boolean) input.get("isPublic");
        Boolean fetchMetadata = input.get("fetchMetadata") != null ?
                (Boolean) input.get("fetchMetadata") : true; // Default to true

        return bookmarkService.createBookmarkFromUrl(url, categoryId, tagIds,
                isFavorite, isPublic, fetchMetadata);
    }

    @MutationMapping
    public Bookmark refreshMetadata(@Argument Long id) {
        return bookmarkService.refreshMetadata(id);
    }

    // Helper method
    private BookmarkFilter mapToBookmarkFilter(Map<String, Object> filterMap) {
        BookmarkFilter filter = new BookmarkFilter();
        filter.setQuery((String) filterMap.get("query"));
        filter.setCategoryId(filterMap.get("categoryId") != null ?
                Long.parseLong(filterMap.get("categoryId").toString()) : null);
        filter.setTagName((String) filterMap.get("tagName"));
        filter.setIsFavorite((Boolean) filterMap.get("isFavorite"));
        filter.setMinRating(filterMap.get("minRating") != null ?
                Integer.parseInt(filterMap.get("minRating").toString()) : null);
        filter.setIsPublic((Boolean) filterMap.get("isPublic"));
        return filter;
    }
}
