package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.List;

import lombok.Data;


@Data
public class RoleNoUserDto {
	
	private long id;
	private String name;
	private boolean isDefaultRole;
	private boolean isAdminRole;
	
	private List<PermissionAdminRoleListDto> permissions;
}