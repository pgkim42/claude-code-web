package com.example.bookmark.exception;

/**
 * Thrown when external metadata fetching fails.
 * This is a technical exception, not a business logic error.
 */
public class MetadataFetchException extends BusinessException {

    public MetadataFetchException(String url, Throwable cause) {
        super(
            ErrorCode.METADATA_FETCH_FAILED,
            String.format("Failed to fetch metadata from URL: %s", url),
            cause
        );
    }

    public MetadataFetchException(String message) {
        super(ErrorCode.METADATA_FETCH_FAILED, message);
    }
}
