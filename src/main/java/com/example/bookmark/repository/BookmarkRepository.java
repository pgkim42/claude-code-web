package com.example.bookmark.repository;

import com.example.bookmark.model.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // Basic queries
    List<Bookmark> findByCategoryId(Long categoryId);
    List<Bookmark> findByTitleContainingIgnoreCase(String title);

    // Favorites
    List<Bookmark> findByIsFavoriteTrue();
    Page<Bookmark> findByIsFavoriteTrue(Pageable pageable);

    // Public bookmarks
    List<Bookmark> findByIsPublicTrue();
    Page<Bookmark> findByIsPublicTrue(Pageable pageable);

    // Rating queries
    List<Bookmark> findByRatingGreaterThanEqual(Integer rating);
    Page<Bookmark> findByRatingGreaterThanEqual(Integer rating, Pageable pageable);

    // Advanced search with tags
    @Query("SELECT DISTINCT b FROM Bookmark b LEFT JOIN FETCH b.tags t WHERE " +
           "(:query IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:categoryId IS NULL OR b.category.id = :categoryId) " +
           "AND (:tagName IS NULL OR t.name = :tagName) " +
           "AND (:isFavorite IS NULL OR b.isFavorite = :isFavorite) " +
           "AND (:minRating IS NULL OR b.rating >= :minRating)")
    List<Bookmark> searchBookmarks(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("tagName") String tagName,
            @Param("isFavorite") Boolean isFavorite,
            @Param("minRating") Integer minRating
    );

    // Find bookmarks by tag
    @Query("SELECT DISTINCT b FROM Bookmark b JOIN b.tags t WHERE t.name = :tagName")
    List<Bookmark> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT DISTINCT b FROM Bookmark b JOIN b.tags t WHERE t.id = :tagId")
    List<Bookmark> findByTagId(@Param("tagId") Long tagId);

    // Most visited bookmarks
    @Query("SELECT b FROM Bookmark b ORDER BY b.visitCount DESC")
    List<Bookmark> findMostVisited(Pageable pageable);

    // Recently visited
    @Query("SELECT b FROM Bookmark b WHERE b.lastVisitedAt IS NOT NULL ORDER BY b.lastVisitedAt DESC")
    List<Bookmark> findRecentlyVisited(Pageable pageable);

    // Statistics
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.isFavorite = true")
    Long countFavorites();

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.category.id = :categoryId")
    Long countByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT COALESCE(SUM(b.visitCount), 0) FROM Bookmark b")
    Long getTotalVisits();

    @Query("SELECT COALESCE(AVG(b.rating), 0.0) FROM Bookmark b WHERE b.rating IS NOT NULL")
    Double getAverageRating();

    // Optimized category statistics - solves N+1 problem
    @Query("SELECT new com.example.bookmark.dto.CategoryStatistics(c.id, c.name, COUNT(b.id)) " +
           "FROM Category c LEFT JOIN c.bookmarks b " +
           "GROUP BY c.id, c.name " +
           "ORDER BY c.name")
    List<com.example.bookmark.dto.CategoryStatistics> getCategoryStatistics();
}
