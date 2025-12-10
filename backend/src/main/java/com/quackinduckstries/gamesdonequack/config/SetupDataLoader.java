package com.quackinduckstries.gamesdonequack.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.config.RoleConfig.RoleDefinition;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent>{

	boolean alreadySetup = false;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final RoleConfig roleConfig;
	
	SetupDataLoader(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleConfig roleConfig){
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
		this.roleConfig = roleConfig;
	}
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(alreadySetup) {
			return;
		}
		
		for(var def : roleConfig.getDefinitions()) {
			for(var perm : def.getPermissions()) {
				createPermissionIfNotFound(perm);
			}
			List<Permission> permissions = def.getPermissions()
					.stream()
					.map(this::createPermissionIfNotFound)
					.toList();
			createRoleIfNotFound(def, permissions);
		}

		alreadySetup = true;
	}
	
	/*The list of permissions associated with the role currently will be checked 
	 * against the permissions that should be associated according to the role 
	 * definition in RoleConfig file. 
	 * Second parameter must be the associated permissions according 
	 * to RoleConfig file.
	 * 
	 */
	List<Permission> getMissingPermissions(Role role, List<Permission> wantedPermissions) {
		List<Permission> missingPermissions = new ArrayList<Permission>();
		Set<String> currentPermissionsSet = new HashSet<>();
		
		Collection<Permission> currentPermissions = role.getPermissions();
		for(var permission : currentPermissions) {
			currentPermissionsSet.add(permission.getName());
		}
		
		for(var permission : wantedPermissions) {
			if(!currentPermissionsSet.contains(permission.getName())) {
				missingPermissions.add(permission);
			}
		}
		return missingPermissions;
	}
	
	@Transactional
	Permission createPermissionIfNotFound(String name) {
		return permissionRepository.findByName(name).orElseGet(() -> {
			Permission permission = new Permission(name);
			return permissionRepository.save(permission);
		});
	}
	
	@Transactional
	Role createRoleIfNotFound(RoleDefinition def, List<Permission> permissions) {
				
		Optional<Role> role = roleRepository.findByName(def.getName());
		if (role.isEmpty()) {
	        Role newRole = new Role(def.getName(), permissions);
	        newRole.setDefaultRole(def.isDefaultRole());
	        newRole.setAdminRole(def.isAdminRole());
	        return roleRepository.save(newRole);
	    }
		Role noOption = role.get();
		
		List<Permission> toAdd = getMissingPermissions(noOption, permissions);
		for(Permission p : toAdd) {
			noOption.addPermission(p);
		}
		
		return noOption;
	}
}
