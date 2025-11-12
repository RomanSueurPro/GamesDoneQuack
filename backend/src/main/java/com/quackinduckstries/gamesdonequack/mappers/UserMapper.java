package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    //UserDto userToUserDto(User user);
    //User userDtoToUser(UserDto dto);
    
//    @Mapping(target = "username", source = "requestedUsername")
//    @Mapping(target = "password", source = "requestedPassword")
//    UserDto registerRequestToUserDto(RegisterRequestDTO request);
}
