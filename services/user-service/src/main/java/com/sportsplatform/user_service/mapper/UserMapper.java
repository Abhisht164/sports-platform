package com.sportsplatform.user_service.mapper;

import com.sportsplatform.user_service.graphql.generated.types.User;
import com.sportsplatform.user_service.projection.UserProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "userCode")
    User toUser(UserProjection u);
}


