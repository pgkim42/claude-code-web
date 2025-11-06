package com.example.bookmark.exception;

/**
 * Thrown when attempting to create a duplicate resource.
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static DuplicateResourceException category(String name) {
        return new DuplicateResourceException(
            ErrorCode.DUPLICATE_CATEGORY,
            String.format("Category already exists with name: %s", name)
        );
    }

    public static DuplicateResourceException tag(String name) {
        return new DuplicateResourceException(
            ErrorCode.DUPLICATE_TAG,
            String.format("Tag already exists with name: %s", name)
        );
    }
}
