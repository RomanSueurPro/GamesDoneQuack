package com.quackinduckstries.gamesdonequack.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;


@Service
public class UserService {

    private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RoleConfig roleConfig;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, RoleConfig roleConfig) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.roleConfig = roleConfig;
	}
	
	
	@Transactional
	public User registerNewUser(RegisterRequestDTO request) {
		User newUser = new User();
		newUser.setUsername(request.getRequestedUsername());
		newUser.setPassword(passwordEncoder.encode(request.getRequestedPassword()));
		
		Role defaultRole = roleRepository.findByName(roleConfig.getDefaultRole())
				.orElseThrow(() -> new IllegalStateException("Default Role not found : " + roleConfig.getDefaultRole()));
		
		newUser.setRole(defaultRole);
		
		userRepository.save(newUser);
		return newUser;
	}
	
}
