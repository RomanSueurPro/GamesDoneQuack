package com.quackinduckstries.gamesdonequack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByName(String name);
	
	Optional<Permission> findById(long id);
	
	void deleteById(long id);

	boolean existsByName(String name);
}
