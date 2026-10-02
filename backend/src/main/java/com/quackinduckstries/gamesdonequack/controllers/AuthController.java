package com.quackinduckstries.gamesdonequack.controllers;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.LoggedInUserDto;
import com.quackinduckstries.gamesdonequack.Dtos.LoginRequestDto;
import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDto;
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
		returnedUser.setBanned(userDetails.isBanned());
		returnedUser.setRoleName(userDetails.getAuthorities()
				.stream().findFirst()
				.orElseThrow(() -> new IllegalStateException("User did not have a role"))
				.getAuthority());
		
		if(userDetails.getUnbanDate() != null) {
			returnedUser.setUnbanDate(userDetails.getUnbanDate());
		}
		
        return ResponseEntity.ok(returnedUser);
	}
	
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(
    		@RequestBody RegisterRequestDto registerRequestDto) throws DuplicateUsernameException {

        userService.registerNewUser(registerRequestDto);
        
        return ResponseEntity.ok(Map.of("message", "New user insertion procedure completed."));
    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request){
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getIdentifier(), loginRequestDto.getPassword());
		
			Authentication authentication = authManager.authenticate(authToken);
			
			CustomUserDetails  userDetails = (CustomUserDetails) authentication.getPrincipal();
			
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(authentication);
			
			HttpSession session = request.getSession(true);
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
			
			sessionRegistry.registerNewSession(
		        session.getId(),
		        authentication.getPrincipal()
		    );
			
		if(userDetails.isBanned()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "ACCOUNT_BANNED", "message", "Your account has been banned."));
		}
		return ResponseEntity.ok(Map.of("message", "login successful"));
	}
}
