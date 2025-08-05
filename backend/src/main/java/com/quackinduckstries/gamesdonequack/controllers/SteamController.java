package com.quackinduckstries.gamesdonequack.controllers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class SteamController {

	
	
	@GetMapping("/kaamelott")
	public ResponseEntity<String> getKaamelotData() throws MalformedURLException, IOException{
		String url = "https://kaamelott.chaudie.re/api/all";
		
		RestClient defaultClient = RestClient.create();

		String response = defaultClient.get()
			
			.uri(url)
			
			.retrieve()
			.body(String.class);
			
			
		
		return ResponseEntity.ok(response);
		
		
	}
	
}
