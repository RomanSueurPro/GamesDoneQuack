package com.quackinduckstries.gamesdonequack.Dtos;

//import lombok.Data;

//@Data
public class RegisterRequestDTO {
	
	private String requestedUsername;
	private String requestedPassword;
	
	
	public RegisterRequestDTO(String requestedUsername, String requestedPassword) {
		this.requestedUsername = requestedUsername;
		this.requestedPassword = requestedPassword;
	}
	
	public String getRequestedUsername() {
		return this.requestedUsername;
	}
	public String getRequestedPassword() {
		return this.requestedPassword;
	}
	
}
