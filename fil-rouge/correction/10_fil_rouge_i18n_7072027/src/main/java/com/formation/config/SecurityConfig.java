package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.formation.repository.UserRepository;

@Configuration
//@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private UserRepository appUserRepository; 
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests( authz -> authz
				.requestMatchers("/", "/css/**", "/js/**", "/h2-console/**", "/paging").permitAll() 
				.requestMatchers("/genererDuplicata/**").hasAuthority("ADMIN")
				.anyRequest().authenticated()
				)
		.csrf(httpCsrf -> httpCsrf.disable()) // Désactive CSRF pour simplifier les tests
		.headers(headers -> headers.frameOptions(
				(frame) -> frame.disable())) // Désactive les options de cadre pour H2 console
		.formLogin(Customizer.withDefaults());
		
		return http.build();
	}
	
	
	// Création d'un encodeur de mot de passe pour sécuriser les mots de passe
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
