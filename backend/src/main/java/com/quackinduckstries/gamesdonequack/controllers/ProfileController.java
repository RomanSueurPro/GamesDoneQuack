package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class ProfileController {

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/profile")
	public ResponseEntity<ObjectNode> testRoleUser(){
		
		
		//JSON message code
        ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		String message = "You were able to access";
		json.put("message", message);
		
		return ResponseEntity.ok(json);
	}
	
	
	
}
