package com.quackinduckstries.gamesdonequack.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.LoggedInUserDto;
import com.quackinduckstries.gamesdonequack.config.CustomUserDetails;

@RestController
public class AuthController {
	
	@GetMapping("/api/me")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails  userDetails) {
		if (userDetails == null) {
            return ResponseEntity.ok(null); // Used to be unauthorized but too noisy in console.
        }

        // build response object
		LoggedInUserDto returnedUser = new LoggedInUserDto();
		
		returnedUser.setId(userDetails.getId());
		returnedUser.setUsername(userDetails.getUsername());
		returnedUser.setRoleName(userDetails.getAuthorities()
				.stream().findFirst()
				.orElseThrow(() -> new IllegalStateException("User did not have a role"))
				.getAuthority());
		
        return ResponseEntity.ok(returnedUser);
	}
}
