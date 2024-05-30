package com.javaded.web.mappers;

import com.javaded.domain.user.User;
import com.javaded.web.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto> {

    @Override
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User toEntity(UserDto dto);
}
