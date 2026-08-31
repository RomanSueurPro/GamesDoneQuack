package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
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
}
