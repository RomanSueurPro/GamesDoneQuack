package com.quackinduckstries.gamesdonequack.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;

@RestController
public class AuthController {
	
	@GetMapping("/api/me")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
            return ResponseEntity.status(401).build(); // UNAUTHORIZED :: UNAUTHORIZED :: UNAUTHORIZED
        }

        // build response object
		UserDto returnedUser = new UserDto();
		returnedUser.setUsername(userDetails.getUsername());
		
        return ResponseEntity.ok(returnedUser);
	}
}
