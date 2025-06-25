package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// cette classe va nous permettre de configurer les règles de sécurité
@EnableWebSecurity // activer la configuration de securité
@Configuration // permet à spring de connaitre le bean; et aussi de créer des beans dans le contexte
public class DuplicataSecurityConfig {
	
	
	// urls à whiteslister
	private static final AntPathRequestMatcher []  WEB_WHITE_LIST_URLS = {
			new AntPathRequestMatcher("/"),
			new AntPathRequestMatcher("/css/**"),
			new AntPathRequestMatcher("/js/**"),
			new AntPathRequestMatcher("/user/dto**"),
			new AntPathRequestMatcher("/h2-console/**"),
	};
	
	private static final AntPathRequestMatcher []  WEB_ADMIN_LIST_URLS = {
			new AntPathRequestMatcher("/genererDuplicata**"),
	};
	
	
	@Bean // ce bean contiendra les règles de sécurité 
	public SecurityFilterChain security(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests( authorizeRequests -> { 
				authorizeRequests.requestMatchers(WEB_WHITE_LIST_URLS).permitAll(); // whitelist les urls
				authorizeRequests.requestMatchers(WEB_ADMIN_LIST_URLS).hasRole("ADMIN"); // urls necessitant le role admin
				authorizeRequests.anyRequest().authenticated(); // authentification pour le reste
		})
		.formLogin(Customizer.withDefaults()) // activer le formulaire de login
		.httpBasic(Customizer.withDefaults()) // activer l'authenfication
		.csrf(httpSecuCsrf -> httpSecuCsrf.disable()) // uniquement pour H2 marche
		.headers((headers) -> headers.frameOptions((frame) -> frame.disable()));  // uniquement pour H2 marche
		
		return http.build();
		
	}
	
	// permet d'encoder les mots de passe à la création des users et lors des logins
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
