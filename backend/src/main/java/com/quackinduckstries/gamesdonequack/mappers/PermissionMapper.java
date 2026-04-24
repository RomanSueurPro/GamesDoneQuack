package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionAdminRoleListDto;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

//	Permission permissionDtoToPermission(PermissionDto dto);
	
	@Mapping(target = "roles", ignore = true)
	PermissionDto permissionToPermissionDto(Permission permission);
	
	PermissionAdminRoleListDto permissionToPermissionAdminRoleListDto(Permission permission);
	
}