package com.quackinduckstries.gamesdonequack.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.UserNoRelationsDto;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.exceptions.RoleNotFoundException;
import com.quackinduckstries.gamesdonequack.exceptions.UserNotFoundException;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminUserService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	
	public AdminUserService(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}
	
	@Transactional
	public void updateUser(UserNoRelationsDto updatedUser) {
		User user = userRepository.findById(updatedUser.getId()).orElseThrow(() -> new UserNotFoundException("Could not find user " + updatedUser.getUsername() + " in database"));
		
		user.setDeleteDate(updatedUser.getDeleteDate());
		user.setRole(this.roleRepository.findById(updatedUser.getRole().getId()).orElseThrow(() -> new RoleNotFoundException("Could not find role " + updatedUser.getRole().getName() + " for user update")));
		user.setUsername(updatedUser.getUsername());
	}
	
	
	
}
