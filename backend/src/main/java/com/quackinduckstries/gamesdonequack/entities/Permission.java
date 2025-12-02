package com.quackinduckstries.gamesdonequack.entities;

import java.util.Collection;
import java.util.HashSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "permissions")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "permissions")
	private Collection<Role> roles = new HashSet<>();
	
	public Permission(String name) {
		this.name = name;
	}
	
	protected Permission() {}
	
	public void addRole(Role role) {
		this.roles.add(role);
		role.getPermissions().add(this);
	}
	
}
