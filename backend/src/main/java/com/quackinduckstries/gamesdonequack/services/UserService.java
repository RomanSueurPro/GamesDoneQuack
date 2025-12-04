package com.quackinduckstries.gamesdonequack.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.mappers.UserMapper;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;


@Service
public class UserService {

    private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RoleConfig roleConfig;
	private final UserMapper userMapper;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, RoleConfig roleConfig, UserMapper userMapper) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.roleConfig = roleConfig;
		this.userMapper = userMapper;
	}
	
	
	@Transactional
	public User registerNewUser(RegisterRequestDTO request) throws IllegalStateException, DuplicateUsernameException {
		User newUser = new User();
		newUser.setUsername(request.getRequestedUsername());
		newUser.setPassword(passwordEncoder.encode(request.getRequestedPassword()));
		
		Role defaultRole = roleRepository.findByName(roleConfig.getDefaultRole())
				.orElseThrow(() -> new IllegalStateException("Default Role not found : " + roleConfig.getDefaultRole() + ".\n Please contact support."));
		
		newUser.setRole(defaultRole);
		
		if(userRepository.existsByName(newUser.getUsername())) {
			throw new DuplicateUsernameException("Username " + newUser.getUsername() + " already exists.");
		}
		userRepository.save(newUser);
		return newUser;
	}
	
	@Transactional
	public UserDto deleteUserById(long id) throws IllegalArgumentException{
		User userToBeDeleted = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Could not delete user : user with id=" + id + " not found."));
		userRepository.deleteById(id);
		return userMapper.userToUserDto(userToBeDeleted);
	}
	
	@Transactional
	public UserDto updateUserRole(long idUser, long idRole) throws IllegalArgumentException {
		User userToUpdate = userRepository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("Could not update role of user : user with id=" + idUser + " not found."));
		Role role = roleRepository.findById(idRole).orElseThrow(() -> new IllegalArgumentException("Could not update role of user : role with id=" + idRole + " not found."));
		
		userToUpdate.setRole(role);
		userRepository.flush();
		
		UserDto dto = userMapper.userToUserDto(userToUpdate);
		
		return dto;
	}
	
	
}
