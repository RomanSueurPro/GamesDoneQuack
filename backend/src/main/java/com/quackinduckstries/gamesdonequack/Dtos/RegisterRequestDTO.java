package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestDTO {
	
	private String requestedUsername;
	private String requestedPassword;
	
}
