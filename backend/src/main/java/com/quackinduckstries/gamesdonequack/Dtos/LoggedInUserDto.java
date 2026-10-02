package com.quackinduckstries.gamesdonequack.Dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LoggedInUserDto {
	
	private Long id;

	private String username;
	
	private String roleName;
	
	private boolean isBanned;
	
	private LocalDate unbanDate;
}
