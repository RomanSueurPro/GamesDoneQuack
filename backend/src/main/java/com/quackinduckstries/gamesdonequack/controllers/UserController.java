package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.services.UserService;

@RestController
public class UserController {

    
	private final UserService userService;
	
	public UserController(UserService userService) {
        this.userService = userService;
    }    
	
	@PostMapping("/api/check-username-availability")
	public ResponseEntity<Boolean> checkUsernameAvailability(@RequestBody String name){
		
		if(userService.checkUsernameExistence(name)) {
			return ResponseEntity.ok(false);
		}
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/api/check-email-availability")
	public ResponseEntity<Boolean> checkEmailAvailability(@RequestBody String email){
		
		if(userService.checkEmailExistence(email)) {
			return ResponseEntity.ok(false);
		}
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/getuserprofilebyid")
	public ResponseEntity<?> getUserProfile(@RequestBody long id){
		return ResponseEntity.ok(Map.of("user", userService.getUserProfile(id)));
	}
	
	@PostMapping("/getuserprofilebyusername")
	public ResponseEntity<?> getUserProfile(@RequestBody String username){
		return ResponseEntity.ok(Map.of("user", userService.getUserProfile(username)));
	}
}
