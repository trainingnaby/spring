package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.formation.service.CustomDatabaseUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // activer la sécurité directement
public class WebSecurityConfig {

	@Autowired
	CustomDatabaseUserDetailsService customDatabaseUserDetailsService;
	
	
	// liste des urls sans authentification
	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = {
			new AntPathRequestMatcher("/"),
			new AntPathRequestMatcher("/css/**"),
			new AntPathRequestMatcher("/js/**"),
			new AntPathRequestMatcher("/h2-console/**"),
			new AntPathRequestMatcher("/actuator/**"),
			new AntPathRequestMatcher("/actuator/health**"),
			new AntPathRequestMatcher("/paging/**"),
			new AntPathRequestMatcher("/duplicatasSorted/**"),
			new AntPathRequestMatcher("/flushListDuplicatasCache/**"),
	};
	
	// liste des urls avec authentification et role ADMIN
	private static final AntPathRequestMatcher[] URLS_ADMIN = {
			new AntPathRequestMatcher("/listDuplicatas/**"),
			new AntPathRequestMatcher("/jobs/**"),
	};
	
	private static final AntPathRequestMatcher[] URLS_AUTHENTICATED = {
			new AntPathRequestMatcher("/duplicatas/**"),
			new AntPathRequestMatcher("/duplicatas**"),
			new AntPathRequestMatcher("/duplicatas_dto**"),
			new AntPathRequestMatcher("/debug**"),

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
		.headers((headers) -> headers.frameOptions((frame) -> frame.disable())) // necessaire pour H2
		.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
		.cors(cors -> cors.disable())
		.httpBasic(Customizer.withDefaults())
		.build();
	
	}
	

	// permet de définir la classse qui fera l"encodage des mots de passe
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	// bean qui fera le travail d'extraction d'un user en base de données
	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return customDatabaseUserDetailsService; 
	}
	
	// bean en charge du traitement de login : il va déléguer la recherche d'un user en base au bean de type UserDetailsService
	@Bean
    public DaoAuthenticationProvider authenticationProvider(CustomDatabaseUserDetailsService customDBUserDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService((passwordEncoder()))); 
        auth.setPasswordEncoder(passwordEncoder()); 
        return auth;
    }
	
	
}