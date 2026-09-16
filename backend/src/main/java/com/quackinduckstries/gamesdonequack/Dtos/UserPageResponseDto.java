package com.quackinduckstries.gamesdonequack.Dtos;

import java.util.List;

import lombok.Data;

@Data
public class UserPageResponseDto {
	
	private List<UserNoRelationsDto> users;
	
    private int pageNumber;
    
    private int pageSize;
    
    private long totalElements;
    
    private int totalPages;
}
