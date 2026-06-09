package com.quackinduckstries.gamesdonequack.services;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionWithoutRoleDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.EmptyPermissionNameException;
import com.quackinduckstries.gamesdonequack.exceptions.EmptyRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.mappers.PermissionMapper;
import com.quackinduckstries.gamesdonequack.mappers.RoleMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;

@Service
public class AdminPermissionService {

    private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;
	private final RoleMapper roleMapper;
	private final RoleConfig roleConfig;
	
	
	
	//DO NOT inject adminRoleService here, or the circular dependency malediction would be complete
	//DO NOT inject adminPermissionService here, or the circular dependency malediction would be complete
	public AdminPermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository, PermissionMapper permissionMapper, RoleMapper roleMapper, RoleConfig roleConfig) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.permissionMapper = permissionMapper;
		this.roleMapper = roleMapper;
		this.roleConfig = roleConfig;
		
	}
	
	
	@Transactional
	public PermissionDto createPermission(PermissionDto permissionToCreate) {

		String name = permissionNameValidator(permissionToCreate.getName());
		permissionToCreate.setName(name);
		
		if (permissionRepository.existsByName(name)) {
	        throw new NewPermissionAlreadyExistsException("New permission \"" + name + "\" already exists.");
	    }
		
		Permission createdPermission =  new Permission(name);
		
			
		for(RoleNoRelationsDto role: permissionToCreate.getRoles()) {
			Role completeRole = roleRepository.findById(role.getId()).orElseThrow(() -> new IllegalArgumentException("Error on permission creation : one of the roles was not found"));
			createdPermission.addRole(completeRole);
			if(role.isDefaultRole()) {
				List<String> newDefaultRolePermissionList = new ArrayList<>(roleConfig.getDefaultPermissionNames());
				newDefaultRolePermissionList.add(createdPermission.getName());
				roleConfig.setDefaultPermissionNames(newDefaultRolePermissionList);
			}
		}
		boolean hasAdminRole = permissionToCreate.getRoles()
			    .stream()
			    .anyMatch(RoleNoRelationsDto::isAdminRole);
		if (!hasAdminRole) {
		    Role adminRole = roleRepository.findByIsAdminRoleTrue().orElseThrow(() -> new IllegalStateException("Critical : no admin role in database"));
		    createdPermission.addRole(adminRole);
		}
	    return permissionMapper.permissionToPermissionDto(permissionRepository.save(createdPermission));
	}
	
	
	@Transactional
	public Permission createPermissionIfNotExist(String name) {
		String validName = permissionNameValidator(name);
		return permissionRepository.findByName(validName).orElseGet(() -> {
			Permission permission = new Permission(validName);
			addPermissionToAdminRole(permission);
			return permissionRepository.save(permission);
		});
	}

	
	public Permission getPermissionByName(String existingPermission) {
		return permissionRepository.findByName(existingPermission).orElseThrow(() -> new IllegalArgumentException("Permission not found: \"" + existingPermission + "\"."));
	}
	
	
	@Transactional
	public String deletePermission(PermissionDto permissionToDelete) {
		Permission toDelete = permissionRepository.findById(permissionToDelete.getId()).orElseThrow(() -> new IllegalArgumentException("Could not delete permission : permission not found."));
		var roles = toDelete.getRoles();
		for(var role : roles) {
			role.getPermissions().remove(toDelete);
		}
		
		//Permission is removed from the default permissions set
		if(roleConfig.getDefaultPermissionNames().contains(toDelete.getName())) {
			List<String> updated =new ArrayList<>(roleConfig.getDefaultPermissionNames());
			updated.remove(permissionToDelete.getName());
			roleConfig.setDefaultPermissionNames(updated);
		}
		
		permissionRepository.deleteById(permissionToDelete.getId());
		
		return permissionToDelete.getName();
	}

	
	public boolean existsByName(String name) {
		return permissionRepository.existsByName(name);
	}


	public Permission findByName(String name) {
		return permissionRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Could not find Permissions with name \"" + name + "\"."));
	}


	@Transactional
	public PermissionDto updatePermission(PermissionDto permissionToUpdate) {
		
		String name = permissionNameValidator(permissionToUpdate.getName());
		permissionToUpdate.setName(name);
		
		Permission toUpdate = permissionRepository.findById(permissionToUpdate.getId()).orElseThrow(() -> new IllegalArgumentException("Could not find the permission with id : " + permissionToUpdate.getId() + "."));
		
		//Check availability of new name if name was changed
		if(!toUpdate.getName().equals(name) && permissionRepository.existsByName(name)) {
			
			throw new NewPermissionAlreadyExistsException("Updating permission name to  \"" + name + "\" was denied since there is already a permission with this name.");
		}
		
		//you need a HashSet here or some sort of collection data structure to copy toUpdate.getRoles(), otherwise you would be iterating on a collection that gets modified inside the loop, causing undefined behavior.
		for(Role role: new HashSet<>(toUpdate.getRoles())) {
			toUpdate.removeRole(role);
		}
		
		toUpdate.getRoles().clear();
		
		for(RoleNoRelationsDto role: permissionToUpdate.getRoles()) {
			Role completeRole = roleRepository.findById(role.getId()).orElseThrow(() -> new IllegalArgumentException("Error on permission creation : one of the roles was not found"));
			toUpdate.addRole(completeRole);
		}
		toUpdate.setName(name);
		
		//after updated is set as a list in roleConfig it will become immutable because of the @config bean annotation. Therefore replacing the entire list is the only way to modify some of its elements.
		List<String> newDefaultRolePermissionList =
			    new ArrayList<>(roleConfig.getDefaultPermissionNames());

		newDefaultRolePermissionList.replaceAll(
			    p -> p.equals(toUpdate.getName()) ? name : p
			);
		roleConfig.setDefaultPermissionNames(newDefaultRolePermissionList);
		addPermissionToAdminRole(toUpdate);
		return permissionMapper.permissionToPermissionDto(toUpdate);
	}
	
	public List<PermissionDto> fetchAllPermissions() {

		return permissionRepository.findAll()
				.stream()
				.map((permission)-> permissionMapper.permissionToPermissionDto(permission))
				.toList();
	}
	
	public List<PermissionWithoutRoleDto> fetchAllPermissionsNoRoleField() {
	
		return permissionRepository.findAll()
				.stream()
				.map((permission)-> permissionMapper.permissionToPermissionWithoutRoleDto(permission))
				.toList();
	}

	private String permissionNameValidator(String permissionName) {
		
		String finalName = permissionName.toUpperCase().replaceAll("\\s+", "");
		String tailPattern = "_PERMISSION";
		if(finalName.length() < 11 || !finalName.substring(finalName.length()-11).equals(tailPattern)) {
			finalName = finalName + tailPattern;
		}
		if(finalName.equals(tailPattern)) {
			throw new EmptyPermissionNameException("It is forbidden to create a permission without a name");
		}
		return finalName;
	}
	
	private void addPermissionToAdminRole(Permission permission) {
		Role adminRole = roleRepository.findByIsAdminRoleTrue().orElseThrow(() -> new IllegalStateException("Critical : No admin role in dataBase"));
		adminRole.addPermission(permission);
	}
	
}
