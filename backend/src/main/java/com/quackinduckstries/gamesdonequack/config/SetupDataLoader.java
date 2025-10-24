package com.quackinduckstries.gamesdonequack.config;



import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent>{

	boolean alreadySetup = false;
	
	private final RoleRepository roleRepository;
	
	private final PermissionRepository permissionRepository;
	
	public static Role defaultUserRole;
	
	SetupDataLoader(RoleRepository roleRepository, PermissionRepository permissionRepository){
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}
	
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(alreadySetup) {
			return;
		}
		Permission readPermission = createPermissionIfNotFound("READ_PERMISSION");
		Permission writePermission = createPermissionIfNotFound("WRITE_PERMISSION");
		
		List<Permission> adminPermissions = Arrays.asList(
				readPermission, writePermission);
		createRoleIfNotFound("ROLE_ADMIN", adminPermissions);
		defaultUserRole = createRoleIfNotFound("ROLE_USER", Arrays.asList(readPermission));
		
		alreadySetup = true;
	}
	
	@Transactional
	Permission createPermissionIfNotFound(String name) {
		Permission permission = permissionRepository.findByName(name);
		if(permission == null) {
			permission = new Permission();
			permission.setName(name);
			permissionRepository.save(permission);
		}
		return permission;
	}
	
	@Transactional
	Role createRoleIfNotFound(String name, Collection<Permission> permissions) {
		Role role = roleRepository.findByName(name);
		if(role == null) {
			role = new Role();
			role.setName(name);
			role.setPermissions(permissions);
			roleRepository.save(role);
		}
		return role;
	}
	
}
