package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class LoginRequestDto {

	private String identifier;
	private String password;
}
