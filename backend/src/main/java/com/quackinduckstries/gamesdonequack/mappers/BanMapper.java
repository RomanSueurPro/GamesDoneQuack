package com.quackinduckstries.gamesdonequack.mappers;

import org.mapstruct.Mapper;

import com.quackinduckstries.gamesdonequack.Dtos.BanDto;
import com.quackinduckstries.gamesdonequack.entities.Ban;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface BanMapper {

	BanDto banToBanDto(Ban ban);

	Ban banDtoToBan(BanDto ban);
}
