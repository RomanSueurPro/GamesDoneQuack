package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.Collection;

import lombok.Data;

@Data
public class RoleAdminListDto {
	
	private Long id;
	
	private String name;
	
	private boolean isDefaultRole;
	
	private boolean isAdminRole;
	
	private Collection<PermissionAdminRoleListDto> permissions;
	
	
}
