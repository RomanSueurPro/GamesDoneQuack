package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class LoginController {

	private final AuthenticationManager authManager;
	private final SessionRegistry sessionRegistry;
	
	public LoginController(AuthenticationManager authManager, SessionRegistry sessionRegistry){
		this.authManager = authManager;
		this.sessionRegistry = sessionRegistry;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("username") String username,
            @RequestParam("password") String password, HttpServletRequest request) {
		
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
