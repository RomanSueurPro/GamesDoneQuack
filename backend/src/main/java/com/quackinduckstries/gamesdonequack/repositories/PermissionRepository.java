package com.quackinduckstries.gamesdonequack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Permission findById(long id);
	
	Optional<Permission> findByName(String name);
	
	Optional<Permission> findById(Long id);
	
	void deleteById(Long id);
}
