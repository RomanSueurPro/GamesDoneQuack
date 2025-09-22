package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto dto);
    
    @Mapping(target = "username", source = "requestedUsername")
    @Mapping(target = "password", source = "requestedPassword")
    UserDto registerRequestToUserDto(RegisterRequestDTO request);
}
