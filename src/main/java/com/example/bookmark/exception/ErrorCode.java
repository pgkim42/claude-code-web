package com.example.bookmark.exception;

/**
 * Error codes for business exceptions.
 * Provides structured error information for clients.
 */
public enum ErrorCode {
    // Resource Not Found (404)
    BOOKMARK_NOT_FOUND,
    CATEGORY_NOT_FOUND,
    TAG_NOT_FOUND,

    // Validation Errors (400)
    INVALID_RATING,
    INVALID_URL,
    INVALID_INPUT,

    // Duplicate Resources (409)
    DUPLICATE_CATEGORY,
    DUPLICATE_TAG,

    // Technical Errors (500)
    METADATA_FETCH_FAILED,
    EXTERNAL_SERVICE_ERROR
}
