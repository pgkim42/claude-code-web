package com.example.bookmark.service;

import com.example.bookmark.dto.BookmarkFilter;
import com.example.bookmark.dto.BookmarkStatistics;
import com.example.bookmark.dto.CategoryStatistics;
import com.example.bookmark.dto.UrlMetadata;
import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.Category;
import com.example.bookmark.model.Tag;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UrlMetadataService urlMetadataService;

    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    public Optional<Bookmark> getBookmarkById(Long id) {
        return bookmarkRepository.findById(id);
    }

    public List<Bookmark> getBookmarksByCategory(Long categoryId) {
        return bookmarkRepository.findByCategoryId(categoryId);
    }

    public List<Bookmark> searchBookmarks(String query) {
        return bookmarkRepository.findByTitleContainingIgnoreCase(query);
    }

    @Transactional
    public Bookmark createBookmark(String title, String url, String description, Long categoryId,
                                    List<Long> tagIds, Boolean isFavorite, Integer rating, Boolean isPublic) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        }

        Bookmark bookmark = new Bookmark(title, url, description, category);

        // Set optional fields
        if (isFavorite != null) {
            bookmark.setIsFavorite(isFavorite);
        }
        if (rating != null) {
            validateRating(rating);
            bookmark.setRating(rating);
        }
        if (isPublic != null) {
            bookmark.setIsPublic(isPublic);
        }

        // Add tags
        if (tagIds != null && !tagIds.isEmpty()) {
            Set<Tag> tags = tagIds.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId)))
                    .collect(Collectors.toSet());
            bookmark.setTags(tags);
        }

        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public Bookmark updateBookmark(Long id, String title, String url, String description, Long categoryId,
                                    List<Long> tagIds, Boolean isFavorite, Integer rating, Boolean isPublic) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));

        if (title != null) {
            bookmark.setTitle(title);
        }
        if (url != null) {
            bookmark.setUrl(url);
        }
        if (description != null) {
            bookmark.setDescription(description);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            bookmark.setCategory(category);
        }
        if (isFavorite != null) {
            bookmark.setIsFavorite(isFavorite);
        }
        if (rating != null) {
            validateRating(rating);
            bookmark.setRating(rating);
        }
        if (isPublic != null) {
            bookmark.setIsPublic(isPublic);
        }
        if (tagIds != null) {
            bookmark.getTags().clear();
            Set<Tag> tags = tagIds.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId)))
                    .collect(Collectors.toSet());
            bookmark.setTags(tags);
        }

        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public boolean deleteBookmark(Long id) {
        if (bookmarkRepository.existsById(id)) {
            bookmarkRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Advanced search
    public List<Bookmark> advancedSearch(BookmarkFilter filter) {
        return bookmarkRepository.searchBookmarks(
                filter.getQuery(),
                filter.getCategoryId(),
                filter.getTagName(),
                filter.getIsFavorite(),
                filter.getMinRating()
        );
    }

    // Favorites
    public List<Bookmark> getFavoriteBookmarks() {
        return bookmarkRepository.findByIsFavoriteTrue();
    }

    @Transactional
    public Bookmark toggleFavorite(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));
        bookmark.setIsFavorite(!bookmark.getIsFavorite());
        return bookmarkRepository.save(bookmark);
    }

    // Rating
    @Transactional
    public Bookmark setRating(Long id, Integer rating) {
        validateRating(rating);
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));
        bookmark.setRating(rating);
        return bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getTopRatedBookmarks(Integer minRating) {
        return bookmarkRepository.findByRatingGreaterThanEqual(minRating);
    }

    // Visit tracking
    @Transactional
    public Bookmark recordVisit(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));
        bookmark.recordVisit();
        return bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getMostVisitedBookmarks(Integer limit) {
        return bookmarkRepository.findMostVisited(PageRequest.of(0, limit != null ? limit : 10));
    }

    public List<Bookmark> getRecentlyVisitedBookmarks(Integer limit) {
        return bookmarkRepository.findRecentlyVisited(PageRequest.of(0, limit != null ? limit : 10));
    }

    // Tag operations
    public List<Bookmark> getBookmarksByTag(String tagName) {
        return bookmarkRepository.findByTagName(tagName);
    }

    @Transactional
    public Bookmark addTagToBookmark(Long bookmarkId, Long tagId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + bookmarkId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        bookmark.addTag(tag);
        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public Bookmark removeTagFromBookmark(Long bookmarkId, Long tagId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + bookmarkId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        bookmark.removeTag(tag);
        return bookmarkRepository.save(bookmark);
    }

    // Statistics
    public BookmarkStatistics getBookmarkStatistics() {
        Long totalBookmarks = bookmarkRepository.count();
        Long totalFavorites = bookmarkRepository.countFavorites();
        Long totalVisits = bookmarkRepository.getTotalVisits();
        Double averageRating = bookmarkRepository.getAverageRating();
        Long totalCategories = categoryRepository.count();
        Long totalTags = tagRepository.count();

        return new BookmarkStatistics(
                totalBookmarks,
                totalFavorites,
                totalVisits,
                averageRating,
                totalCategories,
                totalTags
        );
    }

    public List<CategoryStatistics> getCategoryStatistics() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryStatistics(
                        category.getId(),
                        category.getName(),
                        bookmarkRepository.countByCategory(category.getId())
                ))
                .collect(Collectors.toList());
    }

    // URL Metadata Operations

    /**
     * Fetch metadata from URL without creating a bookmark
     */
    public UrlMetadata fetchUrlMetadata(String url) {
        return urlMetadataService.fetchMetadata(url);
    }

    /**
     * Create bookmark from URL with auto-fetched metadata
     */
    @Transactional
    public Bookmark createBookmarkFromUrl(String url, Long categoryId, List<Long> tagIds,
                                          Boolean isFavorite, Boolean isPublic, Boolean fetchMetadata) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUrl(url);

        // Fetch metadata if requested
        if (fetchMetadata != null && fetchMetadata) {
            UrlMetadata metadata = urlMetadataService.fetchMetadata(url);

            // Apply metadata to bookmark
            if (metadata.getTitle() != null) {
                bookmark.setTitle(metadata.getTitle());
            } else {
                bookmark.setTitle(url); // Fallback to URL
            }

            bookmark.setDescription(metadata.getDescription());
            bookmark.setThumbnailUrl(metadata.getThumbnailUrl());
            bookmark.setFaviconUrl(metadata.getFaviconUrl());
            bookmark.setSiteName(metadata.getSiteName());
            bookmark.setAuthor(metadata.getAuthor());
            bookmark.setPublishedDate(metadata.getPublishedDate());
            bookmark.setMetadataFetched(true);
        } else {
            // No metadata fetch - use URL as title
            bookmark.setTitle(url);
            bookmark.setMetadataFetched(false);
        }

        // Set category
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            bookmark.setCategory(category);
        }

        // Set tags
        if (tagIds != null && !tagIds.isEmpty()) {
            Set<Tag> tags = tagIds.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId)))
                    .collect(Collectors.toSet());
            bookmark.setTags(tags);
        }

        // Set optional fields
        if (isFavorite != null) {
            bookmark.setIsFavorite(isFavorite);
        }
        if (isPublic != null) {
            bookmark.setIsPublic(isPublic);
        }

        return bookmarkRepository.save(bookmark);
    }

    /**
     * Refresh metadata for an existing bookmark
     */
    @Transactional
    public Bookmark refreshMetadata(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));

        UrlMetadata metadata = urlMetadataService.fetchMetadata(bookmark.getUrl());

        // Update metadata fields
        if (metadata.getTitle() != null) {
            bookmark.setTitle(metadata.getTitle());
        }
        bookmark.setDescription(metadata.getDescription());
        bookmark.setThumbnailUrl(metadata.getThumbnailUrl());
        bookmark.setFaviconUrl(metadata.getFaviconUrl());
        bookmark.setSiteName(metadata.getSiteName());
        bookmark.setAuthor(metadata.getAuthor());
        bookmark.setPublishedDate(metadata.getPublishedDate());
        bookmark.setMetadataFetched(true);

        return bookmarkRepository.save(bookmark);
    }

    // Helper methods
    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
    }
}
