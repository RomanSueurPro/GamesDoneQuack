package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.RoleDto;
import com.quackinduckstries.gamesdonequack.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	
	Role fromRoleDtoToRole(RoleDto dto);
	RoleDto fromRoleToRoleDto(Role role);
}
