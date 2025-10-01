package com.quackinduckstries.gamesdonequack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quackinduckstries.gamesdonequack.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findById(long id);
	
	Optional<User> findByUsername(String username);
}
