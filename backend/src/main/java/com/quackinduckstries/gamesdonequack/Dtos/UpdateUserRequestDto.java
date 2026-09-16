package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class UpdateUserRequestDto {

	private UserNoRelationsDto user;
	
	private int pageNumber;
}
