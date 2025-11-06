package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.Category;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public void run(String... args) {
        // Create categories
        Category devCategory = new Category("Development", "Programming and development resources");
        Category designCategory = new Category("Design", "Design inspiration and tools");
        Category newsCategory = new Category("News", "Tech news and articles");

        categoryRepository.save(devCategory);
        categoryRepository.save(designCategory);
        categoryRepository.save(newsCategory);

        // Create bookmarks
        bookmarkRepository.save(new Bookmark(
                "Spring Boot Documentation",
                "https://spring.io/projects/spring-boot",
                "Official Spring Boot documentation and guides",
                devCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "GraphQL Official",
                "https://graphql.org/",
                "Learn about GraphQL, how it works, and how to use it",
                devCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "GitHub",
                "https://github.com",
                "Where the world builds software",
                devCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "Dribbble",
                "https://dribbble.com",
                "Discover the world's top designers & creatives",
                designCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "Figma",
                "https://www.figma.com",
                "Collaborative interface design tool",
                designCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "Hacker News",
                "https://news.ycombinator.com",
                "Social news website focusing on computer science",
                newsCategory
        ));

        bookmarkRepository.save(new Bookmark(
                "The Verge",
                "https://www.theverge.com",
                "Technology news and media network",
                newsCategory
        ));

        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("📚 Created " + categoryRepository.count() + " categories");
        System.out.println("🔖 Created " + bookmarkRepository.count() + " bookmarks");
    }
}
