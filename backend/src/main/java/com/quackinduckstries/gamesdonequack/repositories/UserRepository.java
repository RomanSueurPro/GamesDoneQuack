package com.quackinduckstries.gamesdonequack.repositories;

import com.quackinduckstries.gamesdonequack.entities.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long>{

	List<User> findByUsername(String username);
	
	User findById(long id);
	
	User save(User user);
	
}
