package com.quackinduckstries.gamesdonequack.Dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileDto {
	
	private Long id;
	
	private String username;
	
	private String email;
	
	private String bio;
	
	private LocalDate creationDate;
	
	private String avatarPath;
}
