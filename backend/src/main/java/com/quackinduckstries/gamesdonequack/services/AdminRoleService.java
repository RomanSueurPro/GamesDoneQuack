package com.quackinduckstries.gamesdonequack.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionWithoutRoleDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleCompleteDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoUserDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.AlreadyExistingRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.EmptyRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.InvalidNameFormatException;
import com.quackinduckstries.gamesdonequack.mappers.RoleMapper;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminRoleService {

    private final AdminPermissionService adminPermissionService;

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RoleConfig roleConfig;
	private final RoleMapper roleMapper;
	private final SessionRegistry sessionRegistry;
	
	public AdminRoleService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, AdminPermissionService adminPermissionService, RoleConfig roleConfig, RoleMapper roleMapper, SessionRegistry sessionRegistry) {
		this.userRepository = userRepository;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.adminPermissionService = adminPermissionService;
		this.roleConfig = roleConfig;
		this.roleMapper = roleMapper;
		this.sessionRegistry = sessionRegistry;		
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
	public RoleCompleteDto createRole(RoleNoUserDto roleToCreate) {
		
		roleToCreate.setName(roleNameValidator(roleToCreate.getName()));
		if(this.existsByName(roleToCreate.getName())) {
			throw new AlreadyExistingRoleNameException("Role " + roleToCreate.getName() + " already exists");
		}
		
		if(roleToCreate.isAdminRole()) {
			throw new IllegalStateException("Creating a role with admin status is forbidden");
		}
		
		List<Permission> permissions = roleToCreate.getPermissions().stream()
			    .map(p -> adminPermissionService.createPermissionIfNotExist(p.getName()))
			    .toList();
		
		Optional<Role> defaultRole = getDefaultRole();
		
		Role asModel = this.roleMapper.roleNoUserDtoToRole(roleToCreate);
		asModel.setPermissions(new HashSet<Permission>(permissions));
		asModel.setId(null);
		
		if(defaultRole.isEmpty() && roleToCreate.isDefaultRole()) {
			roleConfig.setDefaultRoleName(roleToCreate.getName());
			roleConfig.setDefaultPermissionNames(permissions
					.stream()
					.map((permission)-> permission.getName())
					.toList());
		} 
		else if(defaultRole.isEmpty() && !roleToCreate.isDefaultRole()){
			throw new IllegalStateException("No default Role in database. You must create or update a role to default before anything else.");
		}
		else {
			Role dRole = defaultRole.get();
			if(roleToCreate.isDefaultRole()) {
				setToDefaultRole(asModel, dRole);
			}
		}
		
		this.save(asModel);
	
		return roleMapper.roleToRoleCompleteDto(asModel);
	}

	@Transactional
	public RoleCompleteDto updateRole(RoleNoUserDto roleToUpdate) {                         
				
		Role role = roleRepository.findById(roleToUpdate.getId()).orElseThrow(() -> new IllegalArgumentException("Could not find Role to update."));
		
		roleToUpdate.setName(roleNameValidator(roleToUpdate.getName()));
		
		if(!role.getName().equals(roleToUpdate.getName()) && roleRepository.existsByName(roleToUpdate.getName())) {
			
			throw new AlreadyExistingRoleNameException("Updating role name to  \"" + roleToUpdate.getName() + "\" was denied since there is already a role with this name.");
		}
		
		//Only name update possible for admin role.
		if(roleToUpdate.isAdminRole()) {
			//refresh current admin session and log off all other admins
			InvalidateAllSessionsWithRole(role.getName());
			role.setName(roleToUpdate.getName());
			return roleMapper.roleToRoleCompleteDto(role);
		}
		
		Optional<Role> defaultRole = role.isDefaultRole() ? Optional.ofNullable(role) : getDefaultRole();
		
		
		role.getPermissions().clear();
		
		for(PermissionWithoutRoleDto permission : roleToUpdate.getPermissions()) {
			
			role.addPermission(adminPermissionService.createPermissionIfNotExist(permission.getName()));
		}
		if(defaultRole.isEmpty() && roleToUpdate.isDefaultRole()) {
			roleConfig.setDefaultRoleName(roleToUpdate.getName());
			role.setDefaultRole(true);
			roleConfig.setDefaultPermissionNames(role.getPermissions()
					.stream()
					.map((permission)-> permission.getName())
					.toList());
		} 
		else if(defaultRole.isEmpty() && !roleToUpdate.isDefaultRole()){
			throw new IllegalStateException("No default Role in database. You must update a role to default before anything else.");
			
		}else if(defaultRole.get().getId() != roleToUpdate.getId()) {
			Role dRole = defaultRole.get();
			if(roleToUpdate.isDefaultRole()) {
				setToDefaultRole(role, dRole);
			}
		}
		else {
			if(roleToUpdate.isDefaultRole()) {
				
				List<String> updated =new ArrayList<>(roleToUpdate.getPermissions()
						.stream()
						.map((p) -> p.getName())
						.toList()
						);
				roleConfig.setDefaultPermissionNames(updated);
			}
		}
		
		role.setName(roleToUpdate.getName());
		return roleMapper.roleToRoleCompleteDto(role);
	}
	
	
	private void setToDefaultRole(Role newDefaultRole, Role formerDefaultRole) {
		formerDefaultRole.setDefaultRole(false);
		//This flush is mandatory at this point otherwise hibernate can sometimes violate the unique key constraint for defaultRole. The transaction sql requests order may differ from the order in java code.
		roleRepository.flush();
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


	public List<RoleNoUserDto> fetchAllRoles() {
		return roleRepository.findAll()
				.stream()
				.map((role)-> roleMapper.roleToRoleNoUserDto(role))
				.toList();
	}
	
	public List<RoleNoRelationsDto>fetchAllRolesNoPermissionField(){
		return roleRepository.findAll()
				.stream()
				.map((role)-> roleMapper.roleToRoleNorelationDto(role))
				.toList();
	}

	@Transactional
	public String deleteRole(long id) {
		Role toDelete = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Role was not found in database"));
		
		if(toDelete.isAdminRole()) {
			throw new IllegalStateException("Deleting admin role is not authorized");
		}
		else if(toDelete.isDefaultRole()) {
			throw new IllegalStateException("Deleting default role is not authorized");
		}
		
		roleRepository.delete(toDelete);
		return toDelete.getName();
	}
	
	public void InvalidateAllSessionsWithRole(String roleName) {
		for(Object principal : this.sessionRegistry.getAllPrincipals()) {
			UserDetails user = (UserDetails) principal;
			
			boolean hasRole = user.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals(roleName));
				
			if(hasRole) {
				sessionRegistry.getAllSessions(principal, false)
						.forEach(SessionInformation::expireNow);
			}
		}
	}
	
	private String roleNameValidator(String rolename) {
		String finalName = rolename.toUpperCase().replaceAll("\\s+", "");
		
		String frontPattern = "ROLE_";
		if(finalName.length() < 5 || !finalName.substring(0, 5).equals(frontPattern)) {
			finalName = frontPattern + finalName;
		}
		if(finalName.equals(frontPattern)) {
			throw new EmptyRoleNameException("It is forbidden to create a role without a name");
		}
		regexEnforcer(finalName);
		return finalName;
	}
	
	private void regexEnforcer(String roleName){
		Pattern pattern = Pattern.compile("^(?!_)[A-Za-z0-9_]{1,255}$");
		if(!pattern.matcher(roleName).matches()) {
			throw new InvalidNameFormatException("Attempted role name did not meet format requirements. Only letter, numbers and '_'(not at start) are allowed. Length cannot be more than 250 characters.");
		}
	}
}
