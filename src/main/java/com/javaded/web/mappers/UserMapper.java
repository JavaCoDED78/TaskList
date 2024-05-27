package com.javaded.web.mappers;

import com.javaded.domain.user.User;
import com.javaded.web.dto.user.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto> {
}
