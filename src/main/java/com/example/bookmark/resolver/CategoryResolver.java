package com.example.bookmark.resolver;

import com.example.bookmark.model.Category;
import com.example.bookmark.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

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
    public Category createCategory(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String description = (String) input.get("description");

        return categoryService.create(name, description);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String description = (String) input.get("description");

        return categoryService.update(id, name, description);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryService.delete(id);
    }
}
