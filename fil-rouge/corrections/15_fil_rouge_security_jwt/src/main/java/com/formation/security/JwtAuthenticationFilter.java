package com.formation.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre appele une fois par requete REST.
 *
 * Il lit l'en-tete Authorization: Bearer xxx, valide le token et place
 * l'utilisateur authentifie dans le SecurityContext Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Ici on va extraire le token JWT de l'en-tete Authorization, valider le token et, s'il est valide,
    // Si le ken existe et est valide, on place l'utilisateur authentifie dans le SecurityContext de Spring Security.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // valeur brute du token JWT, sans le prefix "Bearer "
        String token = authorizationHeader.substring(7);
        String username = null;

        try {
            username = jwtService.extractUsername(token);
        } catch (IllegalArgumentException ex) {
            // Token mal forme : on laisse Spring Security renvoyer 401 plus loin.
        }

        // On met le user dans le SecurityContext de Spring Security uniquement si le token est valide et que l'utilisateur n'est pas deja authentifie.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // passer à la chaine suivante
        // Si ce filtre a déja authentifie l'utilisateur, les filtres suivants de Spring Security verront que l'utilisateur 
        // est authentifie et lui permettront d'acceder aux ressources protégées.
        filterChain.doFilter(request, response);
    }
}
