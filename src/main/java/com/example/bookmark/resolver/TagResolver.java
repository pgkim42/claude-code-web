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

@Controller
@RequiredArgsConstructor
public class TagResolver {

    private final TagService tagService;

    @QueryMapping
    public List<Tag> tags() {
        return tagService.getAllTags();
    }

    @QueryMapping
    public Tag tag(@Argument Long id) {
        return tagService.getTagById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    @MutationMapping
    public Tag createTag(@Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String color = (String) input.get("color");

        return tagService.createTag(name, color);
    }

    @MutationMapping
    public Tag updateTag(@Argument Long id, @Argument Map<String, Object> input) {
        String name = (String) input.get("name");
        String color = (String) input.get("color");

        return tagService.updateTag(id, name, color);
    }

    @MutationMapping
    public Boolean deleteTag(@Argument Long id) {
        return tagService.deleteTag(id);
    }
}
