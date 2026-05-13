package com.quackinduckstries.gamesdonequack.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class FindFirstAdminUserService {
	
	private final RoleRepository roleRepository;
	
	private final UserRepository userRepository;
	
	public FindFirstAdminUserService(RoleRepository roleRepository, UserRepository userRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;	
	}
	
	public Optional<User> getFirstAdminIfExists(){
		Role roleAdmin = roleRepository.findFirstByIsAdminRoleTrue().orElseThrow();
		
		return roleAdmin.getUsers().stream().findFirst();
	}
	

}
