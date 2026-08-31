package com.quackinduckstries.gamesdonequack.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.config.RoleConfig;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.exceptions.InvalidEmailFormatException;
import com.quackinduckstries.gamesdonequack.exceptions.InvalidNameFormatException;
import com.quackinduckstries.gamesdonequack.exceptions.InvalidPasswordFormatException;
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
	private final AdminPermissionService adminPermissionService;
	
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, RoleConfig roleConfig, UserMapper userMapper, AdminPermissionService adminPermissionService) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.roleConfig = roleConfig;
		this.userMapper = userMapper;
		this.adminPermissionService = adminPermissionService;
	}
	
	
	@Transactional
	public void registerNewUser(RegisterRequestDto request) throws IllegalStateException, DuplicateUsernameException {
		usernameValidation(request.getUsername());
		passwordValidation(request.getPassword());
			
		User newUser = new User();
		newUser.setUsername(request.getUsername());
		newUser.setPassword(passwordEncoder.encode(request.getPassword()));
		
		if(userRepository.existsByUsername(newUser.getUsername())) {
			throw new DuplicateUsernameException("Username " + newUser.getUsername() + " already exists.");
		}
		
		Role defaultRole;
		Optional<Role> optionalDefaultRole = roleRepository.findByIsDefaultRoleTrue();
		if(optionalDefaultRole.isEmpty()) {
			if(roleRepository.existsByName(roleConfig.getDefaultRoleName())) {
				defaultRole = roleRepository.findByName(roleConfig.getDefaultRoleName()).orElseThrow(() -> new IllegalArgumentException("Default role in database not found in the end."));
			}
			else {
				List<String> defaultPermissionNames = roleConfig.getDefaultPermissionNames();
				List<Permission> defaultPermissions = new ArrayList<>();
				for(var name : defaultPermissionNames) {
					defaultPermissions.add(adminPermissionService.createPermissionIfNotExist(name));
				}
				defaultRole = roleRepository.save(new Role(roleConfig.getDefaultRoleName(), defaultPermissions));
				
			}
			defaultRole.setDefaultRole(true);
		}
		else {
			//maybe not necessary on every pass, for now defaultRole is updated everytime.
			defaultRole = optionalDefaultRole.get();
			roleConfig.setDefaultRoleName(defaultRole.getName());
			roleConfig.setDefaultPermissionNames(defaultRole.getPermissions()
					.stream()
					.map((permission)-> permission.getName())
					.toList());
		}
		
		newUser.setRole(defaultRole);
		
		userRepository.save(newUser);
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
	
	public boolean checkUsernameExistence(String name) {
		return userRepository.existsByUsername(name);
	}
	
	private void usernameValidation(String name) {
		Pattern pattern = Pattern.compile("^[A-Za-z0-9_-]{3,255}$");
		if(!pattern.matcher(name).matches()) {
			throw new InvalidNameFormatException("Attempted user name did not meet format requirements. Only letter, numbers, '_' and '-' are allowed. Length must be between 3 and 255 characters.");
		}
	}
	
	private void passwordValidation(String password) {
		Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,64}$");
		if(!pattern.matcher(password).matches()) {
			throw new InvalidPasswordFormatException("Attempted account password did not meet format requirements. Password must contain at least one of each of the following : lower case letter, upper case letter, number, special character. Length must be between 8 and 64 characters.");
		}
	}

	public boolean checkEmailExistence(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public void emailValidation(String email) {
		Pattern pattern = Pattern.compile(
			    "^(?=.{1,64}$)((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$"
			);
		if(!pattern.matcher(email).matches()) {
			throw new InvalidEmailFormatException("You must enter a valid email address. Length must be below 64 characters.");
		}
	}
}
