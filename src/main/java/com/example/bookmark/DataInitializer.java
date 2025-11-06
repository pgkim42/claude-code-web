package com.example.bookmark;

import com.example.bookmark.model.Bookmark;
import com.example.bookmark.model.Category;
import com.example.bookmark.model.Tag;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TagRepository tagRepository;

    @Override
    public void run(String... args) {
        // Create categories
        Category devCategory = new Category("Development", "Programming and development resources");
        Category designCategory = new Category("Design", "Design inspiration and tools");
        Category newsCategory = new Category("News", "Tech news and articles");
        Category learningCategory = new Category("Learning", "Educational resources and tutorials");

        categoryRepository.save(devCategory);
        categoryRepository.save(designCategory);
        categoryRepository.save(newsCategory);
        categoryRepository.save(learningCategory);

        // Create tags
        Tag javaTag = new Tag("Java", "#007396");
        Tag springTag = new Tag("Spring", "#6DB33F");
        Tag graphqlTag = new Tag("GraphQL", "#E10098");
        Tag designTag = new Tag("Design", "#FF6B6B");
        Tag uiUxTag = new Tag("UI/UX", "#4ECDC4");
        Tag newsTag = new Tag("News", "#FFD93D");
        Tag tutorialTag = new Tag("Tutorial", "#95E1D3");
        Tag documentationTag = new Tag("Documentation", "#38A3A5");

        tagRepository.save(javaTag);
        tagRepository.save(springTag);
        tagRepository.save(graphqlTag);
        tagRepository.save(designTag);
        tagRepository.save(uiUxTag);
        tagRepository.save(newsTag);
        tagRepository.save(tutorialTag);
        tagRepository.save(documentationTag);

        // Create bookmarks with tags and additional properties
        Bookmark springBoot = new Bookmark(
                "Spring Boot Documentation",
                "https://spring.io/projects/spring-boot",
                "Official Spring Boot documentation and guides",
                devCategory
        );
        springBoot.setIsFavorite(true);
        springBoot.setRating(5);
        springBoot.setVisitCount(25);
        springBoot.setLastVisitedAt(LocalDateTime.now().minusDays(1));
        springBoot.getTags().add(javaTag);
        springBoot.getTags().add(springTag);
        springBoot.getTags().add(documentationTag);
        bookmarkRepository.save(springBoot);

        Bookmark graphqlDoc = new Bookmark(
                "GraphQL Official",
                "https://graphql.org/",
                "Learn about GraphQL, how it works, and how to use it",
                devCategory
        );
        graphqlDoc.setIsFavorite(true);
        graphqlDoc.setRating(5);
        graphqlDoc.setVisitCount(18);
        graphqlDoc.setLastVisitedAt(LocalDateTime.now().minusHours(5));
        graphqlDoc.getTags().add(graphqlTag);
        graphqlDoc.getTags().add(documentationTag);
        bookmarkRepository.save(graphqlDoc);

        Bookmark github = new Bookmark(
                "GitHub",
                "https://github.com",
                "Where the world builds software",
                devCategory
        );
        github.setIsFavorite(true);
        github.setRating(5);
        github.setVisitCount(150);
        github.setLastVisitedAt(LocalDateTime.now().minusMinutes(30));
        bookmarkRepository.save(github);

        Bookmark baeldung = new Bookmark(
                "Baeldung",
                "https://www.baeldung.com",
                "Java, Spring and Web Development tutorials",
                learningCategory
        );
        baeldung.setIsFavorite(false);
        baeldung.setRating(4);
        baeldung.setVisitCount(42);
        baeldung.setLastVisitedAt(LocalDateTime.now().minusDays(3));
        baeldung.getTags().add(javaTag);
        baeldung.getTags().add(springTag);
        baeldung.getTags().add(tutorialTag);
        bookmarkRepository.save(baeldung);

        Bookmark javaDoc = new Bookmark(
                "Java Documentation",
                "https://docs.oracle.com/en/java/",
                "Official Java documentation from Oracle",
                devCategory
        );
        javaDoc.setIsFavorite(false);
        javaDoc.setRating(4);
        javaDoc.setVisitCount(12);
        javaDoc.setLastVisitedAt(LocalDateTime.now().minusDays(7));
        javaDoc.getTags().add(javaTag);
        javaDoc.getTags().add(documentationTag);
        bookmarkRepository.save(javaDoc);

        Bookmark dribbble = new Bookmark(
                "Dribbble",
                "https://dribbble.com",
                "Discover the world's top designers & creatives",
                designCategory
        );
        dribbble.setIsFavorite(true);
        dribbble.setRating(5);
        dribbble.setVisitCount(33);
        dribbble.setLastVisitedAt(LocalDateTime.now().minusDays(2));
        dribbble.getTags().add(designTag);
        dribbble.getTags().add(uiUxTag);
        bookmarkRepository.save(dribbble);

        Bookmark figma = new Bookmark(
                "Figma",
                "https://www.figma.com",
                "Collaborative interface design tool",
                designCategory
        );
        figma.setIsFavorite(true);
        figma.setRating(5);
        figma.setVisitCount(67);
        figma.setLastVisitedAt(LocalDateTime.now().minusHours(12));
        figma.getTags().add(designTag);
        figma.getTags().add(uiUxTag);
        bookmarkRepository.save(figma);

        Bookmark hackerNews = new Bookmark(
                "Hacker News",
                "https://news.ycombinator.com",
                "Social news website focusing on computer science",
                newsCategory
        );
        hackerNews.setIsFavorite(false);
        hackerNews.setRating(4);
        hackerNews.setVisitCount(89);
        hackerNews.setLastVisitedAt(LocalDateTime.now().minusHours(2));
        hackerNews.getTags().add(newsTag);
        bookmarkRepository.save(hackerNews);

        Bookmark theVerge = new Bookmark(
                "The Verge",
                "https://www.theverge.com",
                "Technology news and media network",
                newsCategory
        );
        theVerge.setIsFavorite(false);
        theVerge.setRating(3);
        theVerge.setVisitCount(21);
        theVerge.setLastVisitedAt(LocalDateTime.now().minusDays(5));
        theVerge.getTags().add(newsTag);
        bookmarkRepository.save(theVerge);

        Bookmark mdnDocs = new Bookmark(
                "MDN Web Docs",
                "https://developer.mozilla.org",
                "Resources for developers, by developers",
                learningCategory
        );
        mdnDocs.setIsFavorite(false);
        mdnDocs.setRating(5);
        mdnDocs.setVisitCount(54);
        mdnDocs.setLastVisitedAt(LocalDateTime.now().minusDays(4));
        mdnDocs.getTags().add(documentationTag);
        mdnDocs.getTags().add(tutorialTag);
        bookmarkRepository.save(mdnDocs);

        System.out.println("\n=================================");
        System.out.println("✅ Sample data initialized!");
        System.out.println("=================================");
        System.out.println("📚 Categories: " + categoryRepository.count());
        System.out.println("🔖 Bookmarks: " + bookmarkRepository.count());
        System.out.println("🏷️  Tags: " + tagRepository.count());
        System.out.println("⭐ Favorites: " + bookmarkRepository.countFavorites());
        System.out.println("👁️  Total Visits: " + bookmarkRepository.getTotalVisits());
        System.out.println("⭐ Average Rating: " + String.format("%.2f", bookmarkRepository.getAverageRating()));
        System.out.println("=================================\n");
    }
}
