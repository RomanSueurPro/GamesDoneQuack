package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.List;

import lombok.Data;

@Data
public class PermissionDto {

	private long id;
	
	private String name;
	
	private List<RoleNoRelationsDto> roles;
}
