package com.example.bookmark.service;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.Category;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;

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
    public Bookmark createBookmark(String title, String url, String description, Long categoryId) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        }

        Bookmark bookmark = new Bookmark(title, url, description, category);
        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public Bookmark updateBookmark(Long id, String title, String url, String description, Long categoryId) {
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
}
