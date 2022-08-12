package com.github.sentrionic.olympusblog.mapper;

import com.github.sentrionic.olympusblog.dto.user.AuthDto;
import com.github.sentrionic.olympusblog.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", source = "user.createdAt")
    AuthDto mapEntityToDto(User user);
}
