package com.quackinduckstries.gamesdonequack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quackinduckstries.gamesdonequack.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Permission findById(long id);
	
	Permission findByName(String name);
}
