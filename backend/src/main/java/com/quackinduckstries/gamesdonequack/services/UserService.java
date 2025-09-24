package com.quackinduckstries.gamesdonequack.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}
	
	public User registerNewUser(RegisterRequestDTO request) {
		User newUser = new User();
		newUser.setUsername(request.getRequestedUsername());
		newUser.setPassword(passwordEncoder.encode(request.getRequestedPassword()));
		userRepository.save(newUser);
		
		
		return newUser;
	}
	
}
