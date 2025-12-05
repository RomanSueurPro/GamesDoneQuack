package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.services.UserService;

@RestController
public class UserController {
    

    private final UserService userService;

    public UserController(
    		PasswordEncoder passwordEncoder, UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
    		@RequestParam("username") String username,
            @RequestParam("password") String password) throws DuplicateUsernameException {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        RegisterRequestDTO request = new RegisterRequestDTO(username, password);
        userService.registerNewUser(request);
        
        return ResponseEntity.ok(Map.of("message", "New user insertion procedure completed."));
    }	
}
