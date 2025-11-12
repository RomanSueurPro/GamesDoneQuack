package com.quackinduckstries.gamesdonequack.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.quackinduckstries.gamesdonequack.services.CustomUserDetailsService;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf
            		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            		.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(
                authorizeHttp -> {
//                		authorizeHttp.requestMatchers("/admin").hasRole("ROLE_ADMIN");
                        authorizeHttp.requestMatchers("/home").permitAll();
                        authorizeHttp.requestMatchers("/register").permitAll();
                        authorizeHttp.requestMatchers("/csrf").permitAll();
                        authorizeHttp.requestMatchers("/favicon.svg").permitAll();
                        authorizeHttp.requestMatchers("/error").permitAll();
                        authorizeHttp.anyRequest().authenticated();
                }
            )
            .formLogin(l -> l.defaultSuccessUrl("/coincoin"))
            .httpBasic(Customizer.withDefaults())
            .logout(l -> l.logoutSuccessUrl("/home")
            		.deleteCookies("JSESSIONID"))
            .build();              
    }
    
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http, 
    		PasswordEncoder passwordEncoder,
    		CustomUserDetailsService userDetailsService)
    throws Exception {
    	
    	AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    	authBuilder
    		.userDetailsService(userDetailsService)
    		.passwordEncoder(passwordEncoder);
    	
    	return authBuilder.build();
    }
}
