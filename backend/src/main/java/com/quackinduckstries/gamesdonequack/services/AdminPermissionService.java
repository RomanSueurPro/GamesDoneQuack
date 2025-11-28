package com.quackinduckstries.gamesdonequack.services;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;

@Service
public class AdminPermissionService {

    private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	
	public AdminPermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
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
	
	@Transactional
	public synchronized Permission deletePermission(Long id) {
		Permission toDelete = permissionRepository.findById(id).orElseThrow();
		var roles = toDelete.getRoles();
		for(var role : roles) {
			role.getPermissions().remove(toDelete);
		}
		roles.clear();
		permissionRepository.flush();
		roleRepository.flush();
		permissionRepository.deleteById(id);
		
		return toDelete;
	}
}
