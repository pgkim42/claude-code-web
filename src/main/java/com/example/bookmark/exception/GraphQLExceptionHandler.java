package com.example.bookmark.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Global GraphQL exception handler.
 * Converts Java exceptions to structured GraphQL errors.
 *
 * Benefits:
 * - Consistent error format for all clients
 * - Proper error codes and messages
 * - Hides internal implementation details
 */
@Component
@Slf4j
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BusinessException) {
            return handleBusinessException((BusinessException) ex, env);
        }

        // Handle Spring Security exceptions
        if (ex instanceof AccessDeniedException) {
            return handleAccessDenied((AccessDeniedException) ex, env);
        }

        if (ex instanceof AuthenticationException) {
            return handleAuthenticationException((AuthenticationException) ex, env);
        }

        // Handle IllegalArgumentException (from signup/login)
        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument((IllegalArgumentException) ex, env);
        }

        // Handle IllegalStateException (from ownership checks)
        if (ex instanceof IllegalStateException) {
            return handleIllegalState((IllegalStateException) ex, env);
        }

        // Log unexpected errors
        log.error("Unexpected error in GraphQL resolver", ex);

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred")
                .build();
    }

    private GraphQLError handleAccessDenied(AccessDeniedException ex, DataFetchingEnvironment env) {
        log.debug("Access denied: {}", ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.FORBIDDEN)
                .message("Access denied: " + ex.getMessage())
                .extension("errorCode", "ACCESS_DENIED")
                .build();
    }

    private GraphQLError handleAuthenticationException(AuthenticationException ex, DataFetchingEnvironment env) {
        log.debug("Authentication failed: {}", ex.getMessage());

        String errorCode = ex instanceof BadCredentialsException ?
                "INVALID_CREDENTIALS" : "AUTHENTICATION_REQUIRED";

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.UNAUTHORIZED)
                .message(ex.getMessage())
                .extension("errorCode", errorCode)
                .build();
    }

    private GraphQLError handleIllegalArgument(IllegalArgumentException ex, DataFetchingEnvironment env) {
        log.debug("Validation error: {}", ex.getMessage());

        // Check if it's a security-related error
        String message = ex.getMessage();
        if (message != null && (message.contains("already exists") ||
                               message.contains("Username") ||
                               message.contains("Email"))) {
            String errorCode = message.contains("Username") ?
                    "DUPLICATE_USERNAME" : "DUPLICATE_EMAIL";

            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(message)
                    .extension("errorCode", errorCode)
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(message)
                .extension("errorCode", "INVALID_INPUT")
                .build();
    }

    private GraphQLError handleIllegalState(IllegalStateException ex, DataFetchingEnvironment env) {
        log.debug("Invalid state: {}", ex.getMessage());

        // Check if it's an access control error
        String message = ex.getMessage();
        if (message != null && message.contains("Access denied")) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.FORBIDDEN)
                    .message(message)
                    .extension("errorCode", "ACCESS_DENIED")
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(message)
                .extension("errorCode", "INVALID_INPUT")
                .build();
    }

    private GraphQLError handleBusinessException(BusinessException ex, DataFetchingEnvironment env) {
        ErrorType errorType = mapToGraphQLErrorType(ex);

        log.debug("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        return GraphqlErrorBuilder.newError(env)
                .errorType(errorType)
                .message(ex.getMessage())
                .extension("errorCode", ex.getErrorCodeValue())
                .build();
    }

    private ErrorType mapToGraphQLErrorType(BusinessException ex) {
        return switch (ex.getErrorCode()) {
            case BOOKMARK_NOT_FOUND, CATEGORY_NOT_FOUND, TAG_NOT_FOUND, USER_NOT_FOUND ->
                ErrorType.NOT_FOUND;
            case INVALID_RATING, INVALID_URL, INVALID_INPUT, INVALID_CREDENTIALS ->
                ErrorType.BAD_REQUEST;
            case DUPLICATE_CATEGORY, DUPLICATE_TAG, DUPLICATE_USERNAME, DUPLICATE_EMAIL ->
                ErrorType.BAD_REQUEST;
            case UNAUTHORIZED, AUTHENTICATION_REQUIRED, INVALID_TOKEN, TOKEN_EXPIRED ->
                ErrorType.UNAUTHORIZED;
            case ACCESS_DENIED ->
                ErrorType.FORBIDDEN;
            case METADATA_FETCH_FAILED, EXTERNAL_SERVICE_ERROR ->
                ErrorType.INTERNAL_ERROR;
        };
    }
}
