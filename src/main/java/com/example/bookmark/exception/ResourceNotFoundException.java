package com.example.bookmark.exception;

/**
 * Thrown when a requested resource is not found.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static ResourceNotFoundException bookmark(Long id) {
        return new ResourceNotFoundException(
            ErrorCode.BOOKMARK_NOT_FOUND,
            String.format("Bookmark not found with id: %d", id)
        );
    }

    public static ResourceNotFoundException category(Long id) {
        return new ResourceNotFoundException(
            ErrorCode.CATEGORY_NOT_FOUND,
            String.format("Category not found with id: %d", id)
        );
    }

    public static ResourceNotFoundException tag(Long id) {
        return new ResourceNotFoundException(
            ErrorCode.TAG_NOT_FOUND,
            String.format("Tag not found with id: %d", id)
        );
    }
}
