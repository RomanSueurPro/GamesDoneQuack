package com.quackinduckstries.gamesdonequack.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.RoleAdminListDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.controllers.HomeController;
import com.quackinduckstries.gamesdonequack.controllers.ProfileController;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.exceptions.NewRoleAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.mappers.RoleMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminRoleService {

    private final ProfileController profileController;

    private final HomeController homeController;

    private final AdminPermissionService adminPermissionService;

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RoleConfig roleConfig;
	private final RoleMapper roleMapper;
	
	public AdminRoleService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, AdminPermissionService adminPermissionService, RoleConfig roleConfig, RoleMapper roleMapper, HomeController homeController, ProfileController profileController) {
		this.userRepository = userRepository;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.adminPermissionService = adminPermissionService;
		this.roleConfig = roleConfig;
		this.roleMapper = roleMapper;
		this.homeController = homeController;
		this.profileController = profileController;
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
	public RoleDto createRole(String name, List<String> permissions) {
		
		List<Permission> allPermissions = new ArrayList<Permission>();
//		List<String> errorMessages = new ArrayList<>();

//		if(this.existsByName(name)) {
//			errorMessages.add("Role " + name + " already exists");
//		}
		
		if(this.existsByName(name)) {
			throw new NewRoleAlreadyExistsException("Role " + name + " already exists");
		}
		
		for(String permission : permissions) {
			allPermissions.add(adminPermissionService.createPermissionIfNotExist(permission));
		}
		
		Role role = new Role(name, allPermissions);
		
		this.save(role);
//		for(String existingPermission : existingPermissions) {
//			
//			if(adminPermissionService.existsByName(existingPermission)) {
//				allPermissions.add(adminPermissionService.findByName(existingPermission));
//			}
//			else {
//				errorMessages.add("Existing permission " + existingPermission + " does not exist");
//			}
//		}
//		
//		for(String newPermission : newPermissions) {
//			
//			if(!adminPermissionService.existsByName(newPermission)) {
//				allPermissions.add(adminPermissionService.createPermission(newPermission));
//			}
//			else {
//				errorMessages.add("New permission " + newPermission + " already exists");
//			}
//		}
//		
//		if(!errorMessages.isEmpty()) {
//			throw new MultipleErrorsException(errorMessages);
//		}
		
		return roleMapper.fromRoleToRoleDto(role);
	}

	@Transactional
	public RoleDto updateRole(long id, String name, List<String> permissionNames, boolean isNewDefaultRole) {                         
		
		Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not find Role to update."));	
		
		if(!role.getName().equals(name) && roleRepository.existsByName(name)) {
			
			throw new NewPermissionAlreadyExistsException("Updating role name to  \"" + name + "\" was denied since there is already a role with this name.");
		}
		
		Optional<Role> defaultRole = role.isDefaultRole() ? Optional.ofNullable(role) : getDefaultRole();
		
		if(defaultRole.isEmpty() && isNewDefaultRole) {
			roleConfig.setDefaultRoleName(name);
			role.setDefaultRole(true);
			roleConfig.setDefaultPermissionNames(role.getPermissions()
					.stream()
					.map((permission)-> permission.getName())
					.toList());
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
		roleConfig.setDefaultPermissionNames(
				newDefaultRole.getPermissions()
				.stream()
				.map((permission)-> permission.getName())
				.toList());
	}
	
	private Optional<Role> getDefaultRole() {
		return roleRepository.findByIsDefaultRoleTrue();
	}


	public List<RoleAdminListDto> fetchAllRoles() {
		return roleRepository.findAll()
				.stream()
				.map((role)-> roleMapper.fromRoleToRoleAdminListDto(role))
				.toList();
	}
}
