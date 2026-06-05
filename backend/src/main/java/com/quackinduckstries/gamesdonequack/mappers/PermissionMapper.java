package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionWithoutRoleDto;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	PermissionDto permissionToPermissionDto(Permission permission);
	
	PermissionWithoutRoleDto permissionToPermissionWithoutRoleDto(Permission permission);
	
	Permission permissionWithoutRoleDtoToPermission(PermissionWithoutRoleDto dto);
	
	PermissionDto permissionWithoutRoleDtoToPermissionDto(PermissionWithoutRoleDto dto);
}