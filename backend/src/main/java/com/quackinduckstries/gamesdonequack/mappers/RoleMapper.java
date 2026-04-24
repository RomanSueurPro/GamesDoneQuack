package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.RoleAdminListDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleCompleteDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoRelationsDto;
import com.quackinduckstries.gamesdonequack.entities.Role;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class, UserMapper.class})
public interface RoleMapper {
	
//	Role roleDtoToRole(RoleCompleteDto dto);
	RoleCompleteDto roleToRoleCompleteDto(Role role);
	RoleAdminListDto roleToRoleAdminListDto(Role role);
	RoleNoRelationsDto roleToRoleNorelation(Role role);
	
}
