package com.example.bookmark.repository;

import com.example.bookmark.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.bookmarks")
    List<Tag> findAllWithBookmarks();

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.bookmarks b WHERE b.id = :bookmarkId")
    List<Tag> findByBookmarkId(Long bookmarkId);
}
