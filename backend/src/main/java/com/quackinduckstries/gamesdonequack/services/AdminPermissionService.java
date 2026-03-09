package com.quackinduckstries.gamesdonequack.services;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.mappers.PermissionMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;

@Service
public class AdminPermissionService {

    private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;
	private final RoleConfig roleConfig;
	
	
	//DO NOT inject adminRoleService here, or the circular dependency malediction would be complete
	//DO NOT inject adminPermissionService here, or the circular dependency malediction would be complete
	public AdminPermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository, PermissionMapper permissionMapper, RoleConfig roleConfig) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.permissionMapper = permissionMapper;
		this.roleConfig = roleConfig;
	}
	
	
	@Transactional
	public PermissionDto createPermission(String name, List<Long> idRoles) {

		if (permissionRepository.existsByName(name)) {
	        throw new NewPermissionAlreadyExistsException("New permission \"" + name + "\" already exists.");
	    }
		
		Permission createdPermission =  new Permission(name);
			
		for(long id: idRoles) {
			Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Error on permission creation : one of the roles was not found"));
			createdPermission.addRole(role);
		}
				
	    return permissionMapper.fromPermissionToPermissionDto(permissionRepository.save(createdPermission));
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
	public PermissionDto deletePermission(Long id) {
		Permission toDelete = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not delete permission : permission not found."));
		var roles = toDelete.getRoles();
		for(var role : roles) {
			role.getPermissions().remove(toDelete);
		}
		
		//Permission is removed from the default permissions set
		if(roleConfig.getDefaultPermissionNames().contains(toDelete.getName())) {
			roleConfig.getDefaultPermissionNames().remove(toDelete.getName());
		}
		
		permissionRepository.deleteById(id);
		
		return permissionMapper.fromPermissionToPermissionDto(toDelete);
	}

	
	public boolean existsByName(String name) {
		return permissionRepository.existsByName(name);
	}


	public Permission findByName(String name) {
		return permissionRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Could not find Permissions with name \"" + name + "\"."));
	}


	@Transactional
	public PermissionDto updatePermission(long id, String newName, List<Long> idRoles) {
		Permission toUpdate = permissionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not find the permission with id : " + id + "."));
		
		//Check availability of new name if name was changed
		if(!toUpdate.getName().equals(newName) && permissionRepository.existsByName(newName)) {
			
			throw new NewPermissionAlreadyExistsException("Updating permission name to  \"" + newName + "\" was denied since there is already a permission with this name.");
		}
		
		//you need a HashSet here or some sort of collection data structure to copy toUpdate.getRoles(), otherwise you would be iterating on a collection that gets modified inside the loop, causing undefined behavior.
		for(Role role: new HashSet<>(toUpdate.getRoles())) {
			toUpdate.removeRole(role);
		}
		
		toUpdate.getRoles().clear();
		
		for(long idRole: idRoles) {
			Role role = roleRepository.findById(idRole).orElseThrow(() -> new IllegalArgumentException("Error on permission update : one of the roles was not found"));
			toUpdate.addRole(role);
		}
		
		//If permission was in roleConfig
		roleConfig.getDefaultPermissionNames()
	    .replaceAll(p -> p.equals(toUpdate.getName()) ? newName : p);
		
		toUpdate.setName(newName);
		
		return permissionMapper.fromPermissionToPermissionDto(toUpdate);
	}
	
	public List<PermissionDto> fetchAllPermissions() {
		
		return permissionRepository.findAll()
				.stream()
				.map((permission)-> permissionMapper.fromPermissionToPermissionDto(permission))
				.toList();
	}
}
