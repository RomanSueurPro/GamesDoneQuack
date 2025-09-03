package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	
	@GetMapping("/api/me")
	public ResponseEntity<Boolean> checkAuth() {
		boolean response = true;
		
		return ResponseEntity.ok(response);
	}
	
	
}
