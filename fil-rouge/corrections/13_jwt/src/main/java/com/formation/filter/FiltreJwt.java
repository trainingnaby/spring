package com.formation.filter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.formation.service.CustomDatabaseUserDetailsService;
import com.formation.service.JwtServiceAsym;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FiltreJwt extends OncePerRequestFilter {

	@Autowired
	private JwtServiceAsym jwtServiceAsym;

	@Autowired
	private CustomDatabaseUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {

	    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
	    if (header == null || !header.startsWith("Bearer ")) {
	        chain.doFilter(request, response);
	        return;
	    }

	    final String token = header.substring(7);
	    final String username = jwtServiceAsym.validateTokenAndGetUsername(token);
	    if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
	        chain.doFilter(request, response);
	        return;
	    }

	    List<String> roles = jwtServiceAsym.getRoles(token);

	    List<SimpleGrantedAuthority> authorities = roles.stream()
	            .map(SimpleGrantedAuthority::new)
	            .collect(Collectors.toList());

	    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	            username, null, authorities);
	    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	    SecurityContextHolder.getContext().setAuthentication(authToken);

	    chain.doFilter(request, response);
	}

}
