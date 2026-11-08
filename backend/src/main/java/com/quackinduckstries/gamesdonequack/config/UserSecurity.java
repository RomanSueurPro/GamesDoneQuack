package com.quackinduckstries.gamesdonequack.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

	public boolean isOwner(Long userId, Authentication authentication) {
		if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
			return false;
		}
		
		return userDetails.getId().equals(userId);
	}
}
