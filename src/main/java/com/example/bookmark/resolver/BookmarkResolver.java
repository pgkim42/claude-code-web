package com.example.bookmark.resolver;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BookmarkResolver {

    private final BookmarkService bookmarkService;

    @QueryMapping
    public List<Bookmark> bookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @QueryMapping
    public Bookmark bookmark(@Argument Long id) {
        return bookmarkService.getBookmarkById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + id));
    }

    @QueryMapping
    public List<Bookmark> bookmarksByCategory(@Argument Long categoryId) {
        return bookmarkService.getBookmarksByCategory(categoryId);
    }

    @QueryMapping
    public List<Bookmark> searchBookmarks(@Argument String query) {
        return bookmarkService.searchBookmarks(query);
    }

    @MutationMapping
    public Bookmark createBookmark(@Argument Map<String, Object> input) {
        String title = (String) input.get("title");
        String url = (String) input.get("url");
        String description = (String) input.get("description");
        Long categoryId = input.get("categoryId") != null ?
                Long.parseLong(input.get("categoryId").toString()) : null;

        return bookmarkService.createBookmark(title, url, description, categoryId);
    }

    @MutationMapping
    public Bookmark updateBookmark(@Argument Long id, @Argument Map<String, Object> input) {
        String title = (String) input.get("title");
        String url = (String) input.get("url");
        String description = (String) input.get("description");
        Long categoryId = input.get("categoryId") != null ?
                Long.parseLong(input.get("categoryId").toString()) : null;

        return bookmarkService.updateBookmark(id, title, url, description, categoryId);
    }

    @MutationMapping
    public Boolean deleteBookmark(@Argument Long id) {
        return bookmarkService.deleteBookmark(id);
    }
}
