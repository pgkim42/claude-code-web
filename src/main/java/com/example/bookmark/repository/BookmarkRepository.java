package com.example.bookmark.repository;

import com.example.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByCategoryId(Long categoryId);
    List<Bookmark> findByTitleContainingIgnoreCase(String title);
}
