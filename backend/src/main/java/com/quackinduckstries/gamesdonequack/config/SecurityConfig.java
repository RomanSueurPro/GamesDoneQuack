package com.quackinduckstries.gamesdonequack.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
	
	@Value("${security.enable-basic:false}")
	private boolean enableBasic;

	@Value("${security.disable-csrf:false}")
	private boolean disableCsrf;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
    	if(disableCsrf) {
    		http.csrf((csrf) -> csrf.disable());
    	}else {
    		http.csrf(csrf -> csrf
            		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            		.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
    	}
    	    	
		http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            authorizeHttp -> {
//                		authorizeHttp.requestMatchers("/admin").hasRole("ROLE_ADMIN");
                    authorizeHttp.requestMatchers("/home").permitAll();
                    authorizeHttp.requestMatchers("/login").permitAll();
                    authorizeHttp.requestMatchers("/register").permitAll();
                    authorizeHttp.requestMatchers("/csrf").permitAll();
                    authorizeHttp.requestMatchers("/favicon.svg").permitAll();
                    authorizeHttp.requestMatchers("/error").permitAll();
                    authorizeHttp.requestMatchers("/api/me").permitAll();
                    authorizeHttp.anyRequest().authenticated();
            }
        );
		
		
        //.formLogin(AbstractHttpConfigurer::disable)
//            .httpBasic(Customizer.withDefaults()) //Only enable for Postman testing
		
		if(enableBasic) {
			http
				.httpBasic(Customizer.withDefaults());
		}
		
        http.logout(l -> l.logoutSuccessUrl("/home")
        		.deleteCookies("JSESSIONID"));
            
		
            
//		System.out.println("enable basic : " + enableBasic);
//		System.out.println("disable csrf : " + disableCsrf);
        
        return http.build();              
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
