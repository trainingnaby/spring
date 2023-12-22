package com.formation.filter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.formation.service.JwtService;
import com.formation.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Permet de récupérer le JWT sur la requête et de faire les validations nécessaires
@Component
public class FiltreJwt extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserInfoService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		// Récupération du header Authorization
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		//Recuperation du token dans le header Authorization et extraction du username
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}

		// si le securityContext ne contient pas de user authentifié
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// chargement du user en base de données
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			// Valider le contenu du token en le croisant avec les informations en base de données
			if (jwtService.validateToken(token, userDetails)) {
				// Ajouter les infos d'authentification dans le security context
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		// au filtre suivant
		filterChain.doFilter(request, response);
	}
}
