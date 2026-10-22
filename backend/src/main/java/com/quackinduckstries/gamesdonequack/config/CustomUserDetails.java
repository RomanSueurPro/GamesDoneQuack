package com.quackinduckstries.gamesdonequack.config;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.quackinduckstries.gamesdonequack.entities.User;

public class CustomUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4457208825340721975L;
	private Long id;
	private String userName;
	private String userPassword;
	private boolean isBanned;
	private Collection<? extends GrantedAuthority> authorities;
	private LocalDate unbanDate;
	
	
	public CustomUserDetails(User user, boolean isBanned, LocalDate unbanDate){
		this.id = user.getId();
		this.userName = user.getUsername();
		this.userPassword = user.getPassword();
		this.isBanned = isBanned;
		this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
		this.unbanDate = unbanDate;
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return userPassword;
	}

	@Override
	public String getUsername() {
		return userName;
	}
	
	public boolean isBanned() {
		return isBanned;
	}
	
	public LocalDate getUnbanDate() {
		return this.unbanDate;
	}
}
