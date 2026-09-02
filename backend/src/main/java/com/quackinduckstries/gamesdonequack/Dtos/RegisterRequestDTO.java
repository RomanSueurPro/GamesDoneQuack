package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class RegisterRequestDto {
	
	private String username;
	private String email;
	private String password;
}
