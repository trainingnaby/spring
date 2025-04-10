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
	AuthentificationSalesforceIdentity authentificationSalesforceIdentity;
	
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
			new AntPathRequestMatcher("/jobs/**"),
	};
	
	private static final AntPathRequestMatcher[] URLS_AUTHENTICATED = {
			new AntPathRequestMatcher("/duplicatas/**"),
			new AntPathRequestMatcher("/duplicatas**"),
			new AntPathRequestMatcher("/duplicatas_dto**"),
			new AntPathRequestMatcher("/listDuplicatas/**"),
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
		.authenticationProvider(authentificationSalesforceIdentity) // pluguer le provider de login (via ws) à la filterchain
		.httpBasic(Customizer.withDefaults())
		.build();
	
	}
	
	
}