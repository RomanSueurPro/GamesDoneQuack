package com.quackinduckstries.gamesdonequack.Dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserNoRelationsDto {

	private Long id;
	
	private String username;
	
	private String email;
	
	private RoleNoRelationsDto role;
	
	private LocalDate deleteDate;
}
