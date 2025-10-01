package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	
	@GetMapping("/api/me")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
            return ResponseEntity.status(401).build(); // UNAUTHORIZED :: UNAUTHORIZED
        }

        // build response object
        Map<String, Object> response = Map.of(
            "username", userDetails.getUsername()
            // you can add email, roles, etc. if available
        );

        return ResponseEntity.ok(response);
	}
	
	
}
