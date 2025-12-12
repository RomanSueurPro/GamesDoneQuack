package com.quackinduckstries.gamesdonequack.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.RoleDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.controllers.HomeController;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.MultipleErrorsException;
import com.quackinduckstries.gamesdonequack.mappers.RoleMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminRoleService {

    private final HomeController homeController;

    private final AdminPermissionService adminPermissionService;

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RoleConfig roleConfig;
	private final RoleMapper roleMapper;
	
	public AdminRoleService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, AdminPermissionService adminPermissionService, RoleConfig roleConfig, RoleMapper roleMapper, HomeController homeController) {
		this.userRepository = userRepository;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.adminPermissionService = adminPermissionService;
		this.roleConfig = roleConfig;
		this.roleMapper = roleMapper;
		this.homeController = homeController;
	}
	
	
	@Transactional
	public Role save(Role role) {
		roleRepository.save(role);
		return role;
	}

	
	public boolean existsByName(String name) {
		return roleRepository.existsByName(name);
	}

	@Transactional
	public Role createRole(String name, List<String> existingPermissions, List<String> newPermissions) throws MultipleErrorsException {
		
		List<Permission> allPermissions = new ArrayList<Permission>();
		List<String> errorMessages = new ArrayList<>();

		if(this.existsByName(name)) {
			errorMessages.add("Role " + name + " already exists");
		}
		
		for(String existingPermission : existingPermissions) {
			
			if(adminPermissionService.existsByName(existingPermission)) {
				allPermissions.add(adminPermissionService.findByName(existingPermission));
			}
			else {
				errorMessages.add("Existing permission " + existingPermission + " does not exist");
			}
		}
		
		for(String newPermission : newPermissions) {
			
			if(!adminPermissionService.existsByName(newPermission)) {
				allPermissions.add(adminPermissionService.createPermission(newPermission));
			}
			else {
				errorMessages.add("New permission " + newPermission + " already exists");
			}
		}
		
		if(!errorMessages.isEmpty()) {
			throw new MultipleErrorsException(errorMessages);
		}
		
		return this.save(new Role(name, allPermissions));
	}

	@Transactional
	public RoleDto updateRole(long id, String name, List<String> permissionNames, boolean isNewDefaultRole) {
		
		Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not find Role to update."));									
		
		Optional<Role> defaultRole = role.isDefaultRole() ? Optional.ofNullable(role) : getDefaultRole();
		
		if(defaultRole.isEmpty() && isNewDefaultRole) {
			roleConfig.setDefaultRoleName(name);
			role.setDefaultRole(true);
		} 
		else if(defaultRole.isEmpty() && !isNewDefaultRole){
			throw new IllegalStateException("No default Role in database. You must update a role to default before anything else.");
		}
		else {
			Role dRole = defaultRole.get();
			if(isNewDefaultRole) {
				setToDefaultRole(role, dRole);
			}
		}
		
		role.setName(name);
		role.getPermissions().clear();
		for(String permissionName : permissionNames) {
			Permission permission = adminPermissionService.createPermissionIfNotExist(permissionName);
			role.addPermission(permission);
		}
		
		return roleMapper.fromRoleToRoleDto(role);
	}
	
	private void setToDefaultRole(Role newDefaultRole, Role formerDefaultRole) {
		formerDefaultRole.setDefaultRole(false);
		newDefaultRole.setDefaultRole(true);
		roleConfig.setDefaultRoleName(newDefaultRole.getName());
	}
	
	private Optional<Role> getDefaultRole() {
		return roleRepository.findByIsDefaultRoleTrue();
	}
}
