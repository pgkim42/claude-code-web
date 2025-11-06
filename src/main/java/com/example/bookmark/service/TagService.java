package com.example.bookmark.service;

import com.example.bookmark.exception.DuplicateResourceException;
import com.example.bookmark.exception.ResourceNotFoundException;
import com.example.bookmark.model.Tag;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for tag operations.
 *
 * Simple enough to not require Query/Command separation.
 * Uses custom exceptions for proper error handling.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> findAll() {
        log.debug("Finding all tags");
        return tagRepository.findAll();
    }

    public Tag findById(Long id) {
        log.debug("Finding tag by id: {}", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.tag(id));
    }

    public Tag findByName(String name) {
        log.debug("Finding tag by name: {}", name);
        return tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        com.example.bookmark.exception.ErrorCode.TAG_NOT_FOUND,
                        "Tag not found with name: " + name
                ));
    }

    public List<Tag> findByBookmarkId(Long bookmarkId) {
        log.debug("Finding tags for bookmark id: {}", bookmarkId);
        return tagRepository.findByBookmarkId(bookmarkId);
    }

    @Transactional
    public Tag create(String name, String color) {
        log.info("Creating tag with name: {}", name);

        if (tagRepository.findByName(name).isPresent()) {
            throw DuplicateResourceException.tag(name);
        }

        Tag tag = new Tag(name, color);
        Tag saved = tagRepository.save(tag);

        log.info("Created tag with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Tag update(Long id, String name, String color) {
        log.info("Updating tag id: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.tag(id));

        if (name != null) {
            if (tagRepository.findByName(name).isPresent() && !tag.getName().equals(name)) {
                throw DuplicateResourceException.tag(name);
            }
            tag.setName(name);
        }
        if (color != null) {
            tag.setColor(color);
        }

        Tag updated = tagRepository.save(tag);
        log.info("Updated tag id: {}", id);
        return updated;
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deleting tag id: {}", id);

        if (!tagRepository.existsById(id)) {
            throw ResourceNotFoundException.tag(id);
        }

        tagRepository.deleteById(id);
        log.info("Deleted tag id: {}", id);
        return true;
    }
}
