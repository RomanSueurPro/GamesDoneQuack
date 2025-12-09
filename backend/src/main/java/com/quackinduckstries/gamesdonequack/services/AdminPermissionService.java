package com.quackinduckstries.gamesdonequack.services;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.mappers.PermissionMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;

@Service
public class AdminPermissionService {

    private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;
	
	
	//DO NOT inject adminRoleService here, or the circular dependency malediction would be complete
	public AdminPermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository, PermissionMapper permissionMapper) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.permissionMapper = permissionMapper;
	}
	
	
	@Transactional
	public Permission createPermission(String name) {

		if (permissionRepository.existsByName(name)) {
	        throw new NewPermissionAlreadyExistsException("New permission \"" + name + "\" already exists.");
	    }

	    return permissionRepository.save(new Permission(name));
	}
	
	@Transactional
	public Permission createPermissionIfNotExist(String name) {
		return permissionRepository.findByName(name).orElseGet(() -> {
			Permission permission = new Permission(name);
			return permission;
		});
	}

	
	public Permission getPermissionByName(String existingPermission) {
		return permissionRepository.findByName(existingPermission).orElseThrow(() -> new IllegalArgumentException("Permission not found: \"" + existingPermission + "\"."));
	}
	
	
	@Transactional
	public Permission deletePermission(Long id) {
		Permission toDelete = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not delete permission : permission not found."));
		var roles = toDelete.getRoles();
		for(var role : roles) {
			role.getPermissions().remove(toDelete);
		}
		
		roles.clear();
		permissionRepository.deleteById(id);
		
		return toDelete;
	}

	
	public boolean existsByName(String name) {
		return permissionRepository.existsByName(name);
	}


	public Permission findByName(String name) {
		return permissionRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Could not find Permissions with name \"" + name + "\"."));
	}


	@Transactional
	public PermissionDto updatePermission(long id, String name) {
		Permission toUpdate = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not find the permission with id : " + id + "."));
		
		toUpdate.setName(name);
		
		return permissionMapper.fromPermissionToPermissionDto(toUpdate);
	}
}
