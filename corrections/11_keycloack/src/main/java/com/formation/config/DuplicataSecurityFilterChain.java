package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;

// activer les paramètres de gestion de sécurité

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class DuplicataSecurityFilterChain {

	@Bean // ce bean contiendra les règles de sécurité
	public SecurityFilterChain security(HttpSecurity http) throws Exception {

		http.oauth2Login(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

		http.authorizeHttpRequests(authorizeRequests -> {
			authorizeRequests.requestMatchers("/oauth2", "/login").permitAll();
			authorizeRequests.anyRequest().authenticated();
		});

		http.logout(logout -> logout.logoutSuccessUrl(
				"http://localhost:8080/realms/external/protocol/openid-connect/logout?redirect_uri=http://localhost:8081/"));

		return http.build();

	}

}
