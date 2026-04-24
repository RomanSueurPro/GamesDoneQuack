package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class RoleNoRelationsDto {
	private long id;
	private String name;
	private boolean isDefaultRole;
	private boolean isAdminRole;
	
}
