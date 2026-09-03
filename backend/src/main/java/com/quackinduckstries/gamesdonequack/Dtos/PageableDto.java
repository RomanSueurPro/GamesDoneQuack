package com.quackinduckstries.gamesdonequack.Dtos;

import lombok.Data;

@Data
public class PageableDto {
	
	private int pageNumber;
	private int pageSize;
	private int offset;
}
