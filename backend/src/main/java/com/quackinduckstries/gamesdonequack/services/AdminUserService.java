package com.quackinduckstries.gamesdonequack.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.BanDto;
import com.quackinduckstries.gamesdonequack.Dtos.PageableDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserPageResponseDto;
import com.quackinduckstries.gamesdonequack.config.CustomUserDetails;
import com.quackinduckstries.gamesdonequack.controllers.HomeController;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.entities.Ban;
import com.quackinduckstries.gamesdonequack.exceptions.BanNotFoundException;
import com.quackinduckstries.gamesdonequack.exceptions.RoleNotFoundException;
import com.quackinduckstries.gamesdonequack.exceptions.UserNotFoundException;
import com.quackinduckstries.gamesdonequack.mappers.BanMapper;
import com.quackinduckstries.gamesdonequack.mappers.UserMapper;
import com.quackinduckstries.gamesdonequack.repositories.BanRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class AdminUserService {

    private final HomeController homeController;
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final SessionRegistry sessionRegistry;
	private final BanRepository banRepository;
	private final BanMapper banMapper;
	
	public AdminUserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, SessionRegistry sessionRegistry, HomeController homeController, BanRepository banRepository, BanMapper banMapper) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
		this.sessionRegistry = sessionRegistry;
		this.homeController = homeController;
		this.banRepository = banRepository;
		this.banMapper = banMapper;
	}
	
	@Transactional
	public void updateUser(UserNoRelationsDto updatedUser) {
		User user = userRepository.findById(updatedUser.getId()).orElseThrow(() -> new UserNotFoundException("Could not find user " + updatedUser.getUsername() + " in database"));
		
		user.setDeleteDate(updatedUser.getDeleteDate());
		boolean roleChanged = updatedUser.getRole().getId() != user.getRole().getId();
		user.setRole(this.roleRepository.findById(updatedUser.getRole().getId()).orElseThrow(() -> new RoleNotFoundException("Could not find role " + updatedUser.getRole().getName() + " for user update")));
		user.setUsername(updatedUser.getUsername());
		
		if(roleChanged) {
			System.out.println("role was changed");
			invalidateSessionForUser(user.getId());
		}
	}
	
	public void invalidateSessionForUser(long userId) {
		for(Object principal : this.sessionRegistry.getAllPrincipals()) {
			
			if (principal instanceof CustomUserDetails userDetails
	                && userDetails.getId().equals(userId)) {

	            sessionRegistry.getAllSessions(principal, false)
	                    .forEach(SessionInformation::expireNow);

	            break;
	        }
		}
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
	
	public BanDto fetchUserLastBan(UserNoRelationsDto user) {
		
		Optional<Ban> lastBan = banRepository.lastBanForUser(user.getId());
		if(lastBan.isPresent()) {
			return banMapper.banToBanDto(lastBan.get());
		}else {
			return null;
		}
	}
	
	public List<BanDto> fetchAllBansForUser(UserNoRelationsDto user){
		//TODO
		return null;
	}
	
}
