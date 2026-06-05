package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.Collection;

import lombok.Data;

@Data
public class RoleNoUserDto {
	
	private long id;
	
	private String name;
	
	private boolean isDefaultRole;
	
	private boolean isAdminRole;
	
	private Collection<PermissionWithoutRoleDto> permissions;
}