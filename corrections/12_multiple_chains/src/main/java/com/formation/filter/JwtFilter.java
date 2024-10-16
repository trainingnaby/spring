package com.formation.filter;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.formation.config.CustomUserDetailsService;
import com.formation.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	
	@Autowired
	JWTService jwtService;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// extraction du token
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(header == null || !header.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		final String token = header.substring(7);
		final String username = jwtService.validateTokenAndGetUsername(token);
		
		if(username == null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		
		// mise à jour du contexte de sécurité  avec les infos
		
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authtoken);
		
		filterChain.doFilter(request, response);
		
	}
	
	

}
