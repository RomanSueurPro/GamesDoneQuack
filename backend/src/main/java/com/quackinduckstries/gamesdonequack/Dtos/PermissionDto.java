package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.List;

import com.quackinduckstries.gamesdonequack.entities.Role;

import lombok.Data;

@Data
public class PermissionDto {

	private long id;
	private String name;
	
	private List<Role> roles;
}
