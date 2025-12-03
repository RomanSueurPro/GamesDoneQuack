package com.quackinduckstries.gamesdonequack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findById(long id);
	Optional<Role> findByName(String name);
	boolean existsByName(String name);
}
