package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	// liste des urls sans authentification
	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = {
			new AntPathRequestMatcher("/"),
			new AntPathRequestMatcher("/css/**"),
			new AntPathRequestMatcher("/js/**"),
			new AntPathRequestMatcher("/h2-console/**"),
			new AntPathRequestMatcher("/actuator/**"),
			new AntPathRequestMatcher("/listDuplicatas/**"),
			new AntPathRequestMatcher("/paging/**"),
			new AntPathRequestMatcher("/duplicatasSorted/**"),
	};
	
	// liste des urls avec authentification et role ADMIN
	private static final AntPathRequestMatcher[] URLS_ADMIN = {
			new AntPathRequestMatcher("/jobs/**"),
	};
	
	private static final AntPathRequestMatcher[] URLS_AUTHENTICATED = {
			new AntPathRequestMatcher("/duplicatas/**"),
			new AntPathRequestMatcher("/duplicatas**"),
			new AntPathRequestMatcher("/duplicatas_dto**"),
	};

	//Configuration de la SecurityFilterChain de Spring
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	   return  http.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITE_LIST_URLS)
				.permitAll()
				.requestMatchers(URLS_ADMIN)
				 .hasRole("ADMIN")
				.requestMatchers(URLS_AUTHENTICATED).authenticated() // autorise l'accés à la liste des urls dans URLS_AUTHENTICATED pour les utilisateurs connectés, peu importe leur role
				.anyRequest().denyAll() // interdit l'accès à tous les autres urls
				)
	    // H2 crée une iframe pour afficher la console, il faut donc désactiver les protections de sécurité
		.headers((headers) -> headers.frameOptions((frame) -> frame.disable())) // necessaire pour H2
		// contexte REST API, donc pas besoin de des protections CSRF
		.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
		// désactiver les protections CORS, pas besoin pour une API REST
		.cors(cors -> cors.disable())
		// authentification par en mode http basic : le frame de login est généré soit par le navigateur,
		// soit par un client REST et les informations de login sont envoyées dans le header Authorization en base64
		.httpBasic(Customizer.withDefaults())
		.build();
	
	}
	
	// bean utilisé pour encoder le mot de passe à la création d'un utilisateur et lors du login
	// sera utilisé en interne par Spring Security pour croiser les mots de passe venant de la base avec celui entré lors du login
	// IMPE
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	// UserDetailsService est un bean impliqué dans le processus d'authentification
	// Si l'application connait gére elle même les utilisateurs (login et mot de passe)
	// Il suffit d'implémenter cette classe qui contiendra le mécanisme pour récuperer
	// le user en base de données avec ses informations
	// InMemoryUserDetailsManager est une implémentation fournie par spring pour 
	// des utilsateurs en mémoire
	
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.builder().username("user").password(passwordEncoder().encode("user"))
				.roles("USER").build();
		UserDetails admin = User.builder().username("admin").password(passwordEncoder().encode("admin")).roles("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user, admin);
	}
}