package com.quackinduckstries.gamesdonequack.Dtos;


import lombok.Data;

@Data
public class SearchUserAdminInputDto {

	private String input;
	private RoleNoRelationsDto roleDto;
	private PageableDto pageableDto;
}
