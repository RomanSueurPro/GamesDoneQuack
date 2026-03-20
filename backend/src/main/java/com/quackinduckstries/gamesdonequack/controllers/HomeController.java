package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quackinduckstries.gamesdonequack.services.AdminRoleNameFinderService;

@RestController
public class HomeController {
	
	private final AdminRoleNameFinderService adminRoleNameFinderService;
	
	HomeController(AdminRoleNameFinderService adminRoleNameFinderService){
		this.adminRoleNameFinderService = adminRoleNameFinderService;
	}
	
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
	    return token;
	}
	
	@GetMapping("/adminrolename")
	public ResponseEntity<Object> fetchAdminRoleName() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		String message = adminRoleNameFinderService.getAdminRoleName();
		json.put("name", message);
		return ResponseEntity.ok(json);
	}
}
