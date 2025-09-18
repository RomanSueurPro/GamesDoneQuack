package com.quackinduckstries.gamesdonequack.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.User;

@SpringBootTest
class UserMapperTest {

	
	@Autowired
	private UserMapper userMapper;
	
	@Test
    void testUserToDto() {
        User user = new User();
        user.setUsername("vince");
        user.setEmail("vince@example.com");
        
        

        UserDto dto = userMapper.userToUserDto(user);

        assertEquals("vince", dto.getUsername());
        assertEquals("vince@example.com", dto.getEmail());
    }

}
