package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.List;

import com.quackinduckstries.gamesdonequack.entities.Permission;

import lombok.Data;

@Data
public class RoleDto {

	private long id;
	private String name;
	
	private List<Permission> permissions;
}
