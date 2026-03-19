package com.quackinduckstries.gamesdonequack.services;

import org.springframework.stereotype.Service;

import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;

@Service
public class AdminRoleNameFinderService {
	
	private final RoleRepository roleRepository;
	
	AdminRoleNameFinderService(RoleRepository roleRepository){
		this.roleRepository = roleRepository;
	}
	
	public String getAdminRoleName() {
		return roleRepository.findByIsAdminRoleTrue().orElseThrow(()-> new IllegalStateException("There is no admin role in database and it is critical")).getName();
	}
	
}
