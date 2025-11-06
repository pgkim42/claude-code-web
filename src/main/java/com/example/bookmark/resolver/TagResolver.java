package com.example.bookmark.resolver;

import com.example.bookmark.dto.CreateTagInput;
import com.example.bookmark.dto.UpdateTagInput;
import com.example.bookmark.model.Tag;
import com.example.bookmark.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    public Tag createTag(@Argument CreateTagInput input) {
        return tagService.create(input.getName(), input.getColor());
    }

    @MutationMapping
    public Tag updateTag(@Argument Long id, @Argument UpdateTagInput input) {
        return tagService.update(id, input.getName(), input.getColor());
    }

    @MutationMapping
    public Boolean deleteTag(@Argument Long id) {
        return tagService.delete(id);
    }
}
