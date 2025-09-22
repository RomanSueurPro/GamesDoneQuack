package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserDto {

	private Long id;
	
	@NonNull
	private String username;
	
	@NonNull
    private String password;

    
}
