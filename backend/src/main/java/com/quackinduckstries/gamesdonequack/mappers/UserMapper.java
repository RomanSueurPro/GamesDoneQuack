package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
    //User userDtoToUser(UserDto dto);
    
//	default UserDto userToUserDto(User user) {
//		UserDto dto = new UserDto();
//		dto.setId(user.getId());
//		dto.setRole(user.getRole());
//		dto.setUsername(user.getUsername());;
//		return dto;
//	}
	
	
//    @Mapping(target = "username", source = "requestedUsername")
//    @Mapping(target = "password", source = "requestedPassword")
//    UserDto registerRequestToUserDto(RegisterRequestDTO request);
}
