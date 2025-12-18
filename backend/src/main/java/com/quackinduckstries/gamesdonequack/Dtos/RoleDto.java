package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.Collection;
import java.util.List;

import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.User;

import lombok.Data;

@Data
public class RoleDto {

	private long id;
	private String name;
	private boolean isDefaultRole;
	private boolean isAdminRole;
	
	private Collection<User> users;
	private List<Permission> permissions;
}
