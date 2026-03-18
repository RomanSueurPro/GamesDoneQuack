package com.quackinduckstries.gamesdonequack.config;

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
	private Collection<? extends GrantedAuthority> authorities;
	
	
	public CustomUserDetails(User user){
		this.id = user.getId();
		this.userName = user.getUsername();
		this.userPassword = user.getPassword();
		this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
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

	
	
	
}
