package com.formation.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

	public static final String HEADER = "X-API-KEY";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String apiKey = request.getHeader(HEADER);

		// Tester s'il n'y pas de processus d'authentification en cours et si la requête contient un header X-API-KEY.
		if (apiKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			if ("alice-key-2026".equals(apiKey)) {
				authentifier("alice", "ROLE_USER");
			} else if ("admin-key-2026".equals(apiKey)) {
				authentifier("admin", "ROLE_USER", "ROLE_ADMIN");
			}
		}

		filterChain.doFilter(request, response);
	}

	private void authentifier(String username, String... authorities) {
		List<SimpleGrantedAuthority> roles = java.util.Arrays.stream(authorities).map(SimpleGrantedAuthority::new)
				.toList();

		// Aprés login, un object Authentication est créé et stocké dans le SecurityContextHolder.
		var authentication = new UsernamePasswordAuthenticationToken(username, null, roles);
		// On va dire à "TOUT LE MONDE" que l'utilisateur est authentifié et qu'il a tel ou tel rôle.
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
