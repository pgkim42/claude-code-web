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
    USER_NOT_FOUND,

    // Validation Errors (400)
    INVALID_RATING,
    INVALID_URL,
    INVALID_INPUT,
    INVALID_CREDENTIALS,

    // Duplicate Resources (409)
    DUPLICATE_CATEGORY,
    DUPLICATE_TAG,
    DUPLICATE_USERNAME,
    DUPLICATE_EMAIL,

    // Authentication/Authorization Errors (401/403)
    UNAUTHORIZED,
    ACCESS_DENIED,
    AUTHENTICATION_REQUIRED,
    INVALID_TOKEN,
    TOKEN_EXPIRED,

    // Technical Errors (500)
    METADATA_FETCH_FAILED,
    EXTERNAL_SERVICE_ERROR
}
