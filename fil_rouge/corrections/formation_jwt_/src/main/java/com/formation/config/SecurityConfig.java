package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.formation.filter.FiltreJwt;
import com.formation.service.UserInfoService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// Filtre custom à ajouter
	@Autowired
	private FiltreJwt filtreJwt;

	// Bean permettant la recherche en base de données
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserInfoService();
	}
	
	
	
	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = { new AntPathRequestMatcher("/authentification/ajouter_user"),
			new AntPathRequestMatcher("/authentification/generer_token/**") };

	private static final AntPathRequestMatcher[] URLS_AUTHENTICATED = { new AntPathRequestMatcher("/authentification/user/**"),
			new AntPathRequestMatcher("/authentification/admin/**")};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth.requestMatchers(WHITE_LIST_URLS).permitAll()
						.requestMatchers(URLS_AUTHENTICATED).authenticated())
				.headers((headers) -> headers.frameOptions((frame) -> frame.disable()))
				.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()).cors(cors -> cors.disable())
				 .sessionManagement(session ->
				 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(filtreJwt, UsernamePasswordAuthenticationFilter.class)
				// enregistrer le provider
				.authenticationProvider(authenticationProvider())
				.build();
	}
	
	

//	// Configuration de Spring Security
//	//TODO Reecrire cette méthode avec la nouvelle syntaxe pour les instructions deprecated
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		return http.csrf().disable()
//				.authorizeHttpRequests()
//				.requestMatchers("/authentification/accueil", "/authentification/ajouter_user", 
//						"/authentification/generer_token").permitAll()
//				.and()
//				.authorizeHttpRequests().requestMatchers("/authentification/user/**").authenticated()
//				.and()
//				.authorizeHttpRequests().requestMatchers("/authentification/admin/**").authenticated()
//				.and()
//				.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//				.and()
//				// enrgistrement du provider
//				.authenticationProvider(authenticationProvider())
//				// Insertion du filtre 
//				.addFilterBefore(filtreJwt, UsernamePasswordAuthenticationFilter.class)
//				.build();
//		
//		
//	}

	// Permet de gérer le hachage des mots de passage
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	// provider d'authentification de type DaoAuthenticationProvider
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		// attribut userDetailsService en charge de la recherche en base de données
		authenticationProvider.setUserDetailsService(userDetailsService());
		// attribut passwordEncoder pour le hashage du pwd
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	// Bean en charge de déclencher le processus d'authentification
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}


}
