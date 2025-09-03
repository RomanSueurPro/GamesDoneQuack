package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//todo supprimer cette ligne superflue
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class HomeController {
	@GetMapping("/home")
	public ResponseEntity<ObjectNode> message(){
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		String message = "coucou";
		json.put("message", message);
		
		
		
		return ResponseEntity.ok(json);
	}
	
	@GetMapping("/coincoin")
	public ResponseEntity<ObjectNode> coin(){
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		String message = "coincoin";
		json.put("message", message);
		
		
		return ResponseEntity.ok(json);
	}
	
	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
	    return token;  // Spring will automatically resolve the CsrfToken from the request attributes and serialize it as JSON
	}
}
