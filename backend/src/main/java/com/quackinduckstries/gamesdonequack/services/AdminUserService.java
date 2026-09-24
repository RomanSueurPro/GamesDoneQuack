package com.quackinduckstries.gamesdonequack.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.PageableDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserPageResponseDto;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.exceptions.RoleNotFoundException;
import com.quackinduckstries.gamesdonequack.exceptions.UserNotFoundException;
import com.quackinduckstries.gamesdonequack.mappers.UserMapper;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminUserService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	
	public AdminUserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
	}
	
	@Transactional
	public void updateUser(UserNoRelationsDto updatedUser) {
		User user = userRepository.findById(updatedUser.getId()).orElseThrow(() -> new UserNotFoundException("Could not find user " + updatedUser.getUsername() + " in database"));
		
		user.setDeleteDate(updatedUser.getDeleteDate());
		user.setRole(this.roleRepository.findById(updatedUser.getRole().getId()).orElseThrow(() -> new RoleNotFoundException("Could not find role " + updatedUser.getRole().getName() + " for user update")));
		user.setUsername(updatedUser.getUsername());
	}
	
	
	public UserPageResponseDto searchUsersByUsername(String searchInput, PageableDto dto){
		
		Pageable pageable = PageRequest.of(
				dto.getPageNumber(),
				dto.getPageSize(),
				Sort.by("username").ascending()
		    );
		
		Page<UserNoRelationsDto> page = userRepository.findByUsernameContainingIgnoreCase(searchInput, pageable).map(userMapper::userToUserNoRelationsDto);
		
		UserPageResponseDto userPageResponseDto = new UserPageResponseDto();
		userPageResponseDto.setUsers(page.getContent());
		userPageResponseDto.setPageNumber(page.getNumber());
		userPageResponseDto.setPageSize(page.getSize());
		userPageResponseDto.setTotalElements(page.getTotalElements());
		userPageResponseDto.setTotalPages(page.getTotalPages());
		
		return userPageResponseDto;
	}
	
	
}
