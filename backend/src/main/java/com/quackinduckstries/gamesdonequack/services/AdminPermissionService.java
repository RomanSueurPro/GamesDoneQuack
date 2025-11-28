package com.quackinduckstries.gamesdonequack.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;

@Service
public class AdminPermissionService {
	private final PermissionRepository permissionRepository;
	
	public AdminPermissionService(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}
	
	@Transactional
	public Permission createPermission(String name) {
		Permission permission = new Permission(name);
		permissionRepository.save(permission);
		
		return permission;
	}

	public Permission getPermissionByName(String existingPermission) throws IllegalArgumentException{
		return permissionRepository.findByName(existingPermission).orElseThrow(() -> new IllegalArgumentException("Permission not found: " + existingPermission));
	}
}
