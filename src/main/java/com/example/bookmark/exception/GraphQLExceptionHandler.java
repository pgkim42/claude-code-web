package com.example.bookmark.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
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

        // Log unexpected errors
        log.error("Unexpected error in GraphQL resolver", ex);

        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred")
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
            case BOOKMARK_NOT_FOUND, CATEGORY_NOT_FOUND, TAG_NOT_FOUND ->
                ErrorType.NOT_FOUND;
            case INVALID_RATING, INVALID_URL, INVALID_INPUT ->
                ErrorType.BAD_REQUEST;
            case DUPLICATE_CATEGORY, DUPLICATE_TAG ->
                ErrorType.BAD_REQUEST;
            case METADATA_FETCH_FAILED, EXTERNAL_SERVICE_ERROR ->
                ErrorType.INTERNAL_ERROR;
        };
    }
}
