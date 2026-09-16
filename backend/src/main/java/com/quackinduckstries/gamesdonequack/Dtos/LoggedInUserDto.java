package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class LoggedInUserDto {
	
	private Long id;

	private String username;
	
	private String roleName;
}
