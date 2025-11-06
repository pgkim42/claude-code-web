package com.example.bookmark.resolver;

import com.example.bookmark.model.Tag;
import com.example.bookmark.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * GraphQL resolver for tag operations.
 * Thin controller - delegates all logic to TagService.
 */
@Controller
@RequiredArgsConstructor
public class TagResolver {

    private final TagService tagService;

    @QueryMapping
    public List<Tag> tags() {
        return tagService.findAll();
    }

    @QueryMapping
    public Tag tag(@Argument Long id) {
        return tagService.findById(id);
    }

    @MutationMapping
    public Tag createTag(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String color = (String) input.get("color");

        return tagService.create(name, color);
    }

    @MutationMapping
    public Tag updateTag(@Argument Long id, @Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String color = (String) input.get("color");

        return tagService.update(id, name, color);
    }

    @MutationMapping
    public Boolean deleteTag(@Argument Long id) {
        return tagService.delete(id);
    }
}
