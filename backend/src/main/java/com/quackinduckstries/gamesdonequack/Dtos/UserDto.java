package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class UserDto {

	private Long id;
	
	private String username;
	
	private RoleNoRelationsDto role;
}
