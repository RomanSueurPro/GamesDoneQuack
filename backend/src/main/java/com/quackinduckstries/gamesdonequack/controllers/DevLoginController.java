package com.quackinduckstries.gamesdonequack.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RestController;
import com.quackinduckstries.gamesdonequack.entities.User;

import com.quackinduckstries.gamesdonequack.services.AdminRoleNameFinderService;
import com.quackinduckstries.gamesdonequack.services.CustomUserDetailsService;
import com.quackinduckstries.gamesdonequack.services.FindFirstAdminUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Profile("dev")
@RestController
public class DevLoginController {
	private final UserDetailsService userDetailsService;
	
	private final FindFirstAdminUserService findFirstAdminUserService;
	
	private final AdminRoleNameFinderService adminRoleNameFinder;
	

    public DevLoginController(CustomUserDetailsService userDetailsService, FindFirstAdminUserService findFirstAdminUserService, AdminRoleNameFinderService adminRoleNameFinder) {
        this.userDetailsService = userDetailsService;
        this.findFirstAdminUserService = findFirstAdminUserService;
        this.adminRoleNameFinder = adminRoleNameFinder;
    }

    @GetMapping("/dev-login")
    public String devLogin(HttpServletRequest request) {

    		List<GrantedAuthority> authorities =
    		    List.of(new SimpleGrantedAuthority(adminRoleNameFinder.getAdminRoleName()));

    		User admin = findFirstAdminUserService.getFirstAdminIfExists().orElseThrow();
    		UserDetails adminDetails = userDetailsService.loadUserByUsername(admin.getUsername());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                			adminDetails,
                			null,
                			authorities
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return "Logged in as JEAN PUTOIS" ;
    }
}
