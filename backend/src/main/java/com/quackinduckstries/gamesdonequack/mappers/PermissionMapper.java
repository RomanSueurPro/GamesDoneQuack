package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	Permission fromPermissionDtoToPermission(PermissionDto dto);
	PermissionDto fromPermissionToPermissionDto(Permission permission);
	
}