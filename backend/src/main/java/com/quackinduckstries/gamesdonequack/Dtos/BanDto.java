package com.quackinduckstries.gamesdonequack.Dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BanDto {

	private long id;
	
	private UserNoRelationsDto user;
	
	private UserNoRelationsDto moderator;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private String reason;
}
