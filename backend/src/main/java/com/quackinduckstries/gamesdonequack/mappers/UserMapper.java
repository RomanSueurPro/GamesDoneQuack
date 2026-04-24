package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quackinduckstries.gamesdonequack.Dtos.LoggedInUserDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserNoRoleDto;
import com.quackinduckstries.gamesdonequack.entities.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "role", ignore = true)
    UserDto userToUserDto(User user);
    UserNoRoleDto userToUserNoRoleDto(User user); 

    
    @Mapping(target = "roleName", source = "role.name")
    LoggedInUserDto userToLoggedInUserDto(User user);
}
