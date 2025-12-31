package com.sportsplatform.user_service.exception.handler;


import com.netflix.graphql.types.errors.ErrorType;
import com.sportsplatform.user_service.exception.UserAlreadyExistsException;
import com.sportsplatform.user_service.exception.UserNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserExceptionResolver
        extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            Throwable ex, DataFetchingEnvironment env) {

        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        if (root instanceof UserAlreadyExistsException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(root.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(Map.of("code", "USER_ALREADY_EXISTS"))
                    .build();
        }

        if (root instanceof UserNotFoundException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(root.getMessage())
                    .errorType(ErrorType.NOT_FOUND)
                    .extensions(Map.of("code", "USER_NOT_FOUND"))
                    .build();
        }

        return null; // default INTERNAL_ERROR
    }
}
