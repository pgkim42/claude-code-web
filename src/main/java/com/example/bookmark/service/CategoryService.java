package com.example.bookmark.service;

import com.example.bookmark.exception.DuplicateResourceException;
import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.model.Category;
import com.example.bookmark.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for category operations.
 *
 * Simple enough to not require Query/Command separation.
 * Uses custom exceptions for proper error handling.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        log.debug("Finding all categories");
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        log.debug("Finding category by id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.category(id));
    }

    public Category findByName(String name) {
        log.debug("Finding category by name: {}", name);
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        com.example.bookmark.exception.ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found with name: " + name
                ));
    }

    @Transactional
    public Category create(String name, String description) {
        log.info("Creating category with name: {}", name);

        if (categoryRepository.findByName(name).isPresent()) {
            throw DuplicateResourceException.category(name);
        }

        Category category = new Category(name, description);
        Category saved = categoryRepository.save(category);

        log.info("Created category with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Category update(Long id, String name, String description) {
        log.info("Updating category id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.category(id));

        if (name != null) {
            if (categoryRepository.findByName(name).isPresent() && !category.getName().equals(name)) {
                throw DuplicateResourceException.category(name);
            }
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }

        Category updated = categoryRepository.save(category);
        log.info("Updated category id: {}", id);
        return updated;
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deleting category id: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw ResourceNotFoundException.category(id);
        }

        categoryRepository.deleteById(id);
        log.info("Deleted category id: {}", id);
        return true;
    }
}
