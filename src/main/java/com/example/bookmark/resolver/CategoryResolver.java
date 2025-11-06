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

@Controller
@RequiredArgsConstructor
public class CategoryResolver {

    private final CategoryService categoryService;

    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @MutationMapping
    public Category createCategory(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String description = (String) input.get("description");

        return categoryService.createCategory(name, description);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String description = (String) input.get("description");

        return categoryService.updateCategory(id, name, description);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryService.deleteCategory(id);
    }
}
