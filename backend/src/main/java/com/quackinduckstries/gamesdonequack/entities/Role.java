package com.quackinduckstries.gamesdonequack.entities;

import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	
	private String name;
	
	@OneToMany(mappedBy = "role")
	private Collection<User> users;
	
	@ManyToMany
	@JoinTable(
			name = "roles_permissions",
			joinColumns = @JoinColumn(
					name = "id_role"),
			inverseJoinColumns = @JoinColumn(
					name = "id_permission")
			)
	private Collection<Permission> permissions;
	
}
