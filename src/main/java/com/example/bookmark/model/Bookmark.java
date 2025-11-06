package com.example.bookmark.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookmarks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Many-to-Many relationship with Tags
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "bookmark_tags",
            joinColumns = @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Favorites and Rating
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    // Visit tracking
    @Column(name = "visit_count", nullable = false)
    private Integer visitCount = 0;

    @Column(name = "last_visited_at")
    private LocalDateTime lastVisitedAt;

    // Privacy
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    // URL Metadata (auto-fetched)
    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl; // Open Graph image or preview image

    @Column(name = "favicon_url", length = 1000)
    private String faviconUrl; // Website favicon

    @Column(name = "site_name")
    private String siteName; // e.g., "GitHub", "Medium"

    @Column(name = "author")
    private String author; // Article author if available

    @Column(name = "published_date")
    private LocalDateTime publishedDate; // Article publish date if available

    @Column(name = "metadata_fetched", nullable = false)
    private Boolean metadataFetched = false; // Whether metadata was auto-fetched

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isFavorite == null) isFavorite = false;
        if (isPublic == null) isPublic = true;
        if (visitCount == null) visitCount = 0;
        if (metadataFetched == null) metadataFetched = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Bookmark(String title, String url, String description, Category category) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.category = category;
        this.isFavorite = false;
        this.isPublic = true;
        this.visitCount = 0;
        this.metadataFetched = false;
    }

    // Helper method to record a visit
    public void recordVisit() {
        this.visitCount++;
        this.lastVisitedAt = LocalDateTime.now();
    }

    // Helper method to add tags
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getBookmarks().add(this);
    }

    // Helper method to remove tags
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getBookmarks().remove(this);
    }
}
