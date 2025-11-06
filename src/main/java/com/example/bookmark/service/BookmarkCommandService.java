package com.example.bookmark.service;

import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.exception.ValidationException;
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
 * Command service for bookmark write operations.
 *
 * Follows CQRS pattern - handles only write operations.
 * Benefits:
 * - Clear transactional boundaries
 * - Easier to add validation
 * - Audit logging in one place
 * - Can trigger events/notifications
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookmarkCommandService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * Create a new bookmark
     */
    public Bookmark create(String title, String url, String description, Long categoryId,
                          List<Long> tagIds, Boolean isFavorite, Integer rating, Boolean isPublic) {
        log.info("Creating bookmark with url: {}", url);

        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(title);
        bookmark.setUrl(url);
        bookmark.setDescription(description);

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
        if (rating != null) {
            validateRating(rating);
            bookmark.setRating(rating);
        }
        if (isPublic != null) bookmark.setIsPublic(isPublic);

        Bookmark saved = bookmarkRepository.save(bookmark);
        log.info("Created bookmark with id: {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing bookmark
     */
    public Bookmark update(Long id, String title, String url, String description, Long categoryId,
                          List<Long> tagIds, Boolean isFavorite, Integer rating, Boolean isPublic) {
        log.info("Updating bookmark id: {}", id);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));

        // Update fields
        if (title != null) bookmark.setTitle(title);
        if (url != null) bookmark.setUrl(url);
        if (description != null) bookmark.setDescription(description);

        // Update category
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> ResourceNotFoundException.category(categoryId));
            bookmark.setCategory(category);
        }

        // Update tags - JPA handles collection differences automatically
        if (tagIds != null) {
            Set<Tag> tags = tagIds.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> ResourceNotFoundException.tag(tagId)))
                    .collect(Collectors.toSet());
            bookmark.setTags(tags);
        }

        // Update optional fields
        if (isFavorite != null) bookmark.setIsFavorite(isFavorite);
        if (rating != null) {
            validateRating(rating);
            bookmark.setRating(rating);
        }
        if (isPublic != null) bookmark.setIsPublic(isPublic);

        Bookmark updated = bookmarkRepository.save(bookmark);
        log.info("Updated bookmark id: {}", id);
        return updated;
    }

    /**
     * Delete a bookmark
     */
    public boolean delete(Long id) {
        log.info("Deleting bookmark id: {}", id);

        if (!bookmarkRepository.existsById(id)) {
            throw ResourceNotFoundException.bookmark(id);
        }

        bookmarkRepository.deleteById(id);
        log.info("Deleted bookmark id: {}", id);
        return true;
    }

    /**
     * Toggle favorite status
     */
    public Bookmark toggleFavorite(Long id) {
        log.info("Toggling favorite for bookmark id: {}", id);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));

        bookmark.setIsFavorite(!bookmark.getIsFavorite());
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Set rating for a bookmark
     */
    public Bookmark setRating(Long id, Integer rating) {
        log.info("Setting rating {} for bookmark id: {}", rating, id);

        validateRating(rating);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));

        bookmark.setRating(rating);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Record a visit to a bookmark
     */
    public Bookmark recordVisit(Long id) {
        log.debug("Recording visit for bookmark id: {}", id);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(id));

        bookmark.recordVisit();
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Add a tag to a bookmark
     */
    public Bookmark addTag(Long bookmarkId, Long tagId) {
        log.info("Adding tag {} to bookmark {}", tagId, bookmarkId);

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(bookmarkId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> ResourceNotFoundException.tag(tagId));

        bookmark.addTag(tag);
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Remove a tag from a bookmark
     */
    public Bookmark removeTag(Long bookmarkId, Long tagId) {
        log.info("Removing tag {} from bookmark {}", tagId, bookmarkId);

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> ResourceNotFoundException.bookmark(bookmarkId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> ResourceNotFoundException.tag(tagId));

        bookmark.removeTag(tag);
        return bookmarkRepository.save(bookmark);
    }

    // Helper methods

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw ValidationException.invalidRating(rating);
        }
    }
}
