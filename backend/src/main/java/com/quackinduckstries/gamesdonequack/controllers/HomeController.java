package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//todo supprimer cette ligne superflue
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class HomeController {
	@GetMapping("/home")
	public ResponseEntity<String> message(){
		return ResponseEntity.ok("coucou");
	}
	
	@GetMapping("/coincoin")
	public ResponseEntity<String> coin(){
		return ResponseEntity.ok("coincoin");
	}
	
	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
	    return token;  // Spring will automatically resolve the CsrfToken from the request attributes and serialize it as JSON
	}
}
