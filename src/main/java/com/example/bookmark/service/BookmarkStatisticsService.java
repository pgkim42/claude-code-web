package com.example.bookmark.service;

import com.example.bookmark.dto.BookmarkStatistics;
import com.example.bookmark.dto.CategoryStatistics;
import com.example.bookmark.repository.BookmarkRepository;
import com.example.bookmark.repository.CategoryRepository;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for bookmark statistics and analytics.
 *
 * Single Responsibility: Only handles statistical calculations.
 * Benefits:
 * - Can be cached heavily (statistics change slowly)
 * - Easy to add new metrics
 * - Can be moved to separate read database
 * - Performance optimization in isolation
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookmarkStatisticsService {

    private final BookmarkRepository bookmarkRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * Get overall bookmark statistics
     *
     * Cached for 5 minutes to avoid expensive count queries.
     */
    @Cacheable(value = "overallStatistics")
    public BookmarkStatistics getOverallStatistics() {
        log.debug("Calculating overall bookmark statistics");

        Long totalBookmarks = bookmarkRepository.count();
        Long totalFavorites = bookmarkRepository.countFavorites();
        Long totalVisits = bookmarkRepository.getTotalVisits();
        Double averageRating = bookmarkRepository.getAverageRating();
        Long totalCategories = categoryRepository.count();
        Long totalTags = tagRepository.count();

        return new BookmarkStatistics(
                totalBookmarks,
                totalFavorites,
                totalVisits,
                averageRating,
                totalCategories,
                totalTags
        );
    }

    /**
     * Get statistics per category.
     * Optimized to use single JPQL query instead of N+1 queries.
     *
     * Cached for 5 minutes to avoid expensive aggregation queries.
     */
    @Cacheable(value = "categoryStatistics")
    public List<CategoryStatistics> getCategoryStatistics() {
        log.debug("Calculating category statistics (optimized)");
        return bookmarkRepository.getCategoryStatistics();
    }
}
