package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.RoleCompleteDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoUserDto;
import com.quackinduckstries.gamesdonequack.entities.Role;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class, UserMapper.class})
public interface RoleMapper {
	
	Role roleNoUserDtoToRole(RoleNoUserDto dto);
	RoleCompleteDto roleToRoleCompleteDto(Role role);
	RoleNoUserDto roleToRoleNoUserDto(Role role);
	RoleNoRelationsDto roleToRoleNorelationDto(Role role);
	Role roleNoRelationsDtoToRole(RoleNoRelationsDto dto);
	
}
