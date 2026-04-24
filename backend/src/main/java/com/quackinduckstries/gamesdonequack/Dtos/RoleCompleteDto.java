package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class RoleCompleteDto {
	
	private long id;
	private String name;
	private boolean isDefaultRole;
	private boolean isAdminRole;
	
	private Collection<UserNoRoleDto> users;
	private List<PermissionAdminRoleListDto> permissions;
}
