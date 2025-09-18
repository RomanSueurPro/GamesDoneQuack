package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto dto);
}
