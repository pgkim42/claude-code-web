package com.example.bookmark.service;

import com.example.bookmark.model.Category;
import com.example.bookmark.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category createCategory(String name, String description) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Category already exists with name: " + name);
        }

        Category category = new Category(name, description);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (name != null) {
            if (categoryRepository.findByName(name).isPresent() && !category.getName().equals(name)) {
                throw new RuntimeException("Category already exists with name: " + name);
            }
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
