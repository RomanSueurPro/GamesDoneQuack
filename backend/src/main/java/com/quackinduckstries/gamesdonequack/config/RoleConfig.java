package com.quackinduckstries.gamesdonequack.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "roles")
@Getter
@Setter
public class RoleConfig {

	private String defaultRole;
	private List<RoleDefinition> definitions;
	
	@Getter
	@Setter
	public static class RoleDefinition{
		private String name;
		private String description;
		private List<String> permissions;
	}
}
