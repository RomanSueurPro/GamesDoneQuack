package com.quackinduckstries.gamesdonequack.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BanSecurityFilter extends OncePerRequestFilter{

	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/logout")
            || path.equals("/csrf")
            || path.equals("/api/me");
    }
	
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		SecurityContext context = SecurityContextHolder.getContext();

		Authentication authentication = context.getAuthentication();

		if (authentication != null
		        && authentication.isAuthenticated()
		        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

			if (userDetails.isBanned()) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.setHeader("X-Account-Status", "BANNED");

			    response.getWriter().write("""
			        {
			            "error": "ACCOUNT_BANNED",
			            "message": "Your account has been banned."
			        }
			        """);

			    return;
            }
		}
		
		filterChain.doFilter(request, response);
	}

}
