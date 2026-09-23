package com.quackinduckstries.gamesdonequack.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findById(long id);
	
	Optional<User> findByUsername(String username);

	void deleteById(long id);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
	
	long countByUsernameLessThan(String username);
	
	List<User> findByDeleteDateLessThanEqual(Date date);
}
