package com.quackinduckstries.gamesdonequack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findById(long id);
	Role findByName(String name);
}
