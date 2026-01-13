package com.quackinduckstries.gamesdonequack.entities;

import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@EqualsAndHashCode.Include
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(nullable = false)
	private boolean isDefaultRole;
	
	@Column(nullable = false)
	private boolean isAdminRole;
	
	@OneToMany(mappedBy = "role")
	@JsonManagedReference 
	private Collection<User> users;
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JsonBackReference
	@JoinTable(
			name = "roles_permissions",
			joinColumns = @JoinColumn(
					name = "id_role"),
			inverseJoinColumns = @JoinColumn(
					name = "id_permission")
			)
	private Collection<Permission> permissions= new HashSet<>();
	
	protected Role() {}
	
	public Role(String name, Collection<Permission> permissions) {
	    this.name = name;
	    if (permissions != null) {
	        for (Permission p : permissions) {
	            this.addPermission(p);
	        }
	    }
	}
	
	public void addPermission(Permission permission) {
		this.permissions.add(permission);
		permission.getRoles().add(this);
	}
	
	public Permission removePermission(Permission permission) {
		this.permissions.remove(permission);
		return permission;
	}
	
}
