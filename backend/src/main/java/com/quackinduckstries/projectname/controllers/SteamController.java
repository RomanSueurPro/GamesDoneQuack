package com.quackinduckstries.projectname.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class SteamController {

	
	@GetMapping("/steam")
	public ResponseEntity<String> getSteamData() throws MalformedURLException, IOException{
		String url = "https://kaamelott.chaudie.re/api/all";
		
		HttpURLConnection conn = (HttpURLConnection) new URL (url).openConnection();
		conn.setRequestMethod("GET");
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))){
			
			String response = reader.lines().collect(Collectors.joining());
			return ResponseEntity.ok(response);
		}
	}
	
	
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
