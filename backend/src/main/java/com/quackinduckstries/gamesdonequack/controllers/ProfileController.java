package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.config.CustomUserDetails;

@RestController
public class ProfileController {

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	public ResponseEntity<?> testRoleUser(@AuthenticationPrincipal CustomUserDetails  userDetails){
		
		String username = userDetails.getUsername();
		
		return ResponseEntity.ok(Map.of("message", "Your username is : " + username + " !"));
	}
	
	
	
}
