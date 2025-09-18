package com.quackinduckstries.gamesdonequack.mappers;

import org.springframework.stereotype.Component;

import com.quackinduckstries.gamesdonequack.Dtos.RegisterRequestDTO;
import com.quackinduckstries.gamesdonequack.entities.User;

@Component
public class RegisterRequestMapperManual{

    
    public User toEntity(RegisterRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        //user.setUsername(dto.getUsername());
        //user.setPassword(dto.getPassword());
        return user;
    }
}
