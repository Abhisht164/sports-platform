package com.sportsplatform.user_service.resolver;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.sportsplatform.user_service.dto.CreateUserRequest;
import com.sportsplatform.user_service.graphql.generated.types.User;
import com.sportsplatform.user_service.mapper.UserMapper;
import com.sportsplatform.user_service.service.UserCommandService;
import com.sportsplatform.user_service.service.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserDataFetcher {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserMapper userMapper;
    /**
     * Resolver for Query.users
     */
    @DgsQuery
    public List<User> users(@InputArgument Integer limit,
                            @InputArgument Integer offset) {

        log.debug("Fetching users: limit={}, offset={}", limit, offset);

        return userQueryService.fetchUsers( limit != null ? limit : 20,
                        offset != null ? offset : 0)
                .stream()
                .map(userMapper::toUser)
                .toList();
    }

    @DgsMutation
    public User createUser(@Valid @InputArgument("input") CreateUserRequest input) {

        log.debug("Creating User.... ")  ;
        return userMapper.toUser(userCommandService.createUser(input.getUsername(), input.getEmail(), input.getPassword()));
    }


}
