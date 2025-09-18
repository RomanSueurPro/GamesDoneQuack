package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;

import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@RestController
public class userController {

    
    private final UserRepository userRepository;
    

    userController(UserRepository userRepository) {
        
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> registerUser(
    		@RequestParam("username") String username,
            @RequestParam("password") String password) {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        
        RegisterRequestDTO request = new RegisterRequestDTO(username, password);

        
        return ResponseEntity.ok(true);
    }
	
	
	
}
