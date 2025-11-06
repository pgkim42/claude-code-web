package com.example.bookmark.exception;

/**
 * Thrown when input validation fails.
 * Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends BusinessException {

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static ValidationException invalidRating(Integer rating) {
        return new ValidationException(
            ErrorCode.INVALID_RATING,
            String.format("Rating must be between 1 and 5, got: %d", rating)
        );
    }

    public static ValidationException invalidUrl(String url) {
        return new ValidationException(
            ErrorCode.INVALID_URL,
            String.format("Invalid URL format: %s", url)
        );
    }

    public static ValidationException invalidInput(String message) {
        return new ValidationException(
            ErrorCode.INVALID_INPUT,
            message
        );
    }
}
