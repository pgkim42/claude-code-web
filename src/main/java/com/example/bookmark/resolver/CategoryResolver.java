package com.example.bookmark.resolver;

import com.example.bookmark.dto.CreateCategoryInput;
import com.example.bookmark.dto.UpdateCategoryInput;
import com.example.bookmark.model.Category;
import com.example.bookmark.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for category operations.
 * Thin controller - delegates all logic to CategoryService.
 */
@Controller
@RequiredArgsConstructor
public class CategoryResolver {

    private final CategoryService categoryService;

    @QueryMapping
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.findById(id);
    }

    @MutationMapping
    public Category createCategory(@Argument CreateCategoryInput input) {
        return categoryService.create(input.getName(), input.getDescription());
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument UpdateCategoryInput input) {
        return categoryService.update(id, input.getName(), input.getDescription());
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryService.delete(id);
    }
}
