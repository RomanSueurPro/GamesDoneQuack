package com.quackinduckstries.gamesdonequack.config;

import java.util.Collection;
import java.util.List;
//import java.util.Optional;

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
		
		for(var p : roleConfig.getDefinitions()) {
			for(var perm : p.getPermissions()) {
				createPermissionIfNotFound(perm);
			}
		}
		
		for(var def : roleConfig.getDefinitions()) {
			List<Permission> permissions = def.getPermissions()
					.stream()
					.map(this::createPermissionIfNotFound)
					.toList();
			createRoleIfNotFound(def.getName(), permissions);
		}
			
		
		alreadySetup = true;
	}
	
	@Transactional
	Permission createPermissionIfNotFound(String name) {
		return permissionRepository.findByName(name).orElseGet(() -> {
			Permission permission = new Permission();
			permission.setName(name);
			return permissionRepository.save(permission);
		});
	}
	
	@Transactional
	Role createRoleIfNotFound(String name, Collection<Permission> permissions) {
//		Optional<Role> existingRole = roleRepository.findByName(name);
//		if(existingRole.isPresent()) {
//			return existingRole.get();
//		}
//		Role role = new Role();
//		role.setName(name);
//		role.setPermissions(permissions);
//		roleRepository.save(role);
//		return role;
		
		return roleRepository.findByName(name)
				.orElseGet(() -> {
					Role role = new Role();
					role.setName(name);
					role.setPermissions(permissions);
					return roleRepository.save(role);
				});
	}
	
}
