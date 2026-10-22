package com.quackinduckstries.gamesdonequack.services;

import java.time.LocalDate;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quackinduckstries.gamesdonequack.config.CustomUserDetails;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.BanRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BanRepository banRepository;
    private final UserService userService;

    public CustomUserDetailsService(UserRepository userRepository, BanRepository banRepository, UserService userService) {
        this.userRepository = userRepository;
        this.banRepository = banRepository;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    	
        User user = userRepository.findByUsername(identifier)
        		.or(() -> userRepository.findByEmail(identifier))
            .orElseThrow(() -> new UsernameNotFoundException("Username or email not found: " + identifier));
        
        boolean banned = banRepository.isUserCurrentlyBanned(
        		user.getId(),
        		LocalDate.now()
        	);
        	
        LocalDate unbanDate = null;
        if (banned) {
        		unbanDate = userService.getUnbanDateForUser(user.getId());
        }

        return new CustomUserDetails(user, banned, unbanDate);
    }
}