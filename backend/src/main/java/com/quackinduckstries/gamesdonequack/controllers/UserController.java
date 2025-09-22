package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.mappers.UserMapper;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

@RestController
public class UserController {

    
    private final UserRepository userRepository;
    private final UserMapper mapper;
    

    public UserController(UserRepository userRepository, UserMapper constrMapper) {
        
        this.userRepository = userRepository;
        this.mapper = constrMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> registerUser(
    		@RequestParam("username") String username,
            @RequestParam("password") String password) {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        
        RegisterRequestDTO request = new RegisterRequestDTO(username, password);
        UserDto userDto = mapper.registerRequestToUserDto(request);
        User newUser = mapper.userDtoToUser(userDto);
        
        userRepository.save(newUser);
        
        
        return ResponseEntity.ok(true);
    }
	
	
	
}
