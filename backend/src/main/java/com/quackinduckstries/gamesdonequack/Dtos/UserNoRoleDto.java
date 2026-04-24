package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserNoRoleDto {
	
	private Long id;
	
	@NonNull
	private String username;
}
