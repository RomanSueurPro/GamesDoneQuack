package com.quackinduckstries.gamesdonequack.entities;

import java.util.Collection;



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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@ManyToMany(mappedBy = "permissions")
	private Collection<Role> roles;
	
	public Permission(String name) {
		this.name = name;
	}
	
	protected Permission() {}
	
}
