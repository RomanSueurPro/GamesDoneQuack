package com.quackinduckstries.gamesdonequack.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quackinduckstries.gamesdonequack.Dtos.BanDto;
import com.quackinduckstries.gamesdonequack.entities.Ban;
import com.quackinduckstries.gamesdonequack.mappers.BanMapper;
import com.quackinduckstries.gamesdonequack.repositories.BanRepository;

@Service
public class AdminBanService {

	private final BanRepository banRepository;
	private final BanMapper banMapper;
	
	public AdminBanService(BanRepository banRepository, BanMapper banMapper) {
		this.banRepository = banRepository;
		this.banMapper = banMapper;
	}
	
	@Transactional
	public void updateBan(BanDto dto) {
		banRepository.save(banMapper.banDtoToBan(dto));
	}
	
	@Transactional
	public void createBan(BanDto dto) {
		Ban ban = new Ban();
		ban = banMapper.banDtoToBan(dto);
		ban.setId(null);
		banRepository.save(ban);
	}
	
}
