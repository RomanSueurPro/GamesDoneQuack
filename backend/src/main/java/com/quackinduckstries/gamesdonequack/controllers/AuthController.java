package com.quackinduckstries.gamesdonequack.controllers;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.LoggedInUserDto;
import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.config.CustomUserDetails;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {
	
	
	private final UserService userService;
	private final AuthenticationManager authManager;
	private final SessionRegistry sessionRegistry;
	
	public AuthController(PasswordEncoder passwordEncoder, UserService userService, AuthenticationManager authManager, SessionRegistry sessionRegistry) {
		this.userService = userService;
		this.authManager = authManager;
		this.sessionRegistry = sessionRegistry;
	}
	
	@GetMapping("/api/me")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails  userDetails) {
		if (userDetails == null) {
            return ResponseEntity.ok(null); // Used to be unauthorized but too noisy in console.
        }

        // build response object
		LoggedInUserDto returnedUser = new LoggedInUserDto();
		
		returnedUser.setId(userDetails.getId());
		returnedUser.setUsername(userDetails.getUsername());
		returnedUser.setRoleName(userDetails.getAuthorities()
				.stream().findFirst()
				.orElseThrow(() -> new IllegalStateException("User did not have a role"))
				.getAuthority());
		
        return ResponseEntity.ok(returnedUser);
	}
	
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(
    		@RequestParam("username") String username,
            @RequestParam("password") String password) throws DuplicateUsernameException {

        RegisterRequestDTO request = new RegisterRequestDTO(username, password);
        userService.registerNewUser(request);
        
        return ResponseEntity.ok(Map.of("message", "New user insertion procedure completed."));
    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("username") String username,
            @RequestParam("password") String password, HttpServletRequest request){
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		
			Authentication authentication = authManager.authenticate(authToken);
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(authentication);
			
			HttpSession session = request.getSession(true);
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
			
			sessionRegistry.registerNewSession(
		        session.getId(),
		        authentication.getPrincipal()
		    );
			
		return ResponseEntity.ok(Map.of("message", "login successful"));
	}
}
