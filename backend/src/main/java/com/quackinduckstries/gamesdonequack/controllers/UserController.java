package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.services.UserService;

@RestController
public class UserController {
    

    private final UserService userService;

    public UserController(
    		PasswordEncoder passwordEncoder, UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ObjectNode> registerUser(
    		@RequestParam("username") String username,
            @RequestParam("password") String password) {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        RegisterRequestDTO request = new RegisterRequestDTO(username, password);
        userService.registerNewUser(request);
        
        ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		String message = "User new user insertion procedure completed";
		json.put("message", message);
        
        return ResponseEntity.ok(json);
    }	
}
