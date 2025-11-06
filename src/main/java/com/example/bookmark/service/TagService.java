package com.example.bookmark.service;

import com.example.bookmark.model.Tag;
import com.example.bookmark.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    public List<Tag> getTagsForBookmark(Long bookmarkId) {
        return tagRepository.findByBookmarkId(bookmarkId);
    }

    @Transactional
    public Tag createTag(String name, String color) {
        if (tagRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Tag already exists with name: " + name);
        }

        Tag tag = new Tag(name, color);
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(Long id, String name, String color) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        if (name != null) {
            if (tagRepository.findByName(name).isPresent() && !tag.getName().equals(name)) {
                throw new RuntimeException("Tag already exists with name: " + name);
            }
            tag.setName(name);
        }
        if (color != null) {
            tag.setColor(color);
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public boolean deleteTag(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
