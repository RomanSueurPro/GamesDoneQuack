package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class UserNoRelationsDto {

	private Long id;
	
	@NonNull
	private String username;
	
	@NonNull
	private String email;
	
	private RoleNoRelationsDto role;
	
}
