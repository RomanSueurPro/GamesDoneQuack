package com.quackinduckstries.gamesdonequack.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

	@Bean
    public PasswordEncoder passwordEncoder() {
		int strength = 16; //On a "modern" CPU (late 2025) 16 rounds of bycrypt
		//should correspond to about one second of human time.
		
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(strength);
		
		Map<String, PasswordEncoder> encoders = Collections.singletonMap("bcrypt", bcrypt);
		
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }
}
