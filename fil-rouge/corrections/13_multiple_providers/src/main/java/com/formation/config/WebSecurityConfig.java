package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

import com.formation.filter.CustomHeaderAuthenticationFilter;
import com.formation.filter.CustomHeaderAuthenticationProvider;
import com.formation.filter.FiltreJwt;
import com.formation.service.CustomDatabaseUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // activer la sécurité directement
public class WebSecurityConfig {

	@Autowired
	CustomDatabaseUserDetailsService customDatabaseUserDetailsService;
	
	@Autowired
	private FiltreJwt filtreJwt;
	
	@Autowired
	private CustomHeaderAuthenticationProvider customHeaderAuthProvider;


	
	
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
			new AntPathRequestMatcher("/token**"),
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
		
		CustomHeaderAuthenticationFilter customFilter = new CustomHeaderAuthenticationFilter(authenticationManager(http));
        
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
		 .sessionManagement(session ->
		 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // gestion de session statelesss : JWT
			.addFilterBefore(filtreJwt, UsernamePasswordAuthenticationFilter.class) // ajouter un nouveau filtre qui se chargera de l'interception du jwt
			.addFilterBefore(customFilter, FiltreJwt.class)
		// enregistrer le provider
		.authenticationProvider(authenticationProvider())
		.authenticationProvider(customHeaderAuthProvider)
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
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService((passwordEncoder()))); 
        auth.setPasswordEncoder(passwordEncoder()); 
        return auth;
    }
	
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .authenticationProvider(customHeaderAuthProvider)
                .build();
    }
	
	// Dans le cas ou votre schéma de base de données, notamment les tables users et authorities respecte un schéma standard : 
	
	 /*CREATE TABLE users (
			  username VARCHAR(50) NOT NULL PRIMARY KEY,
			  password VARCHAR(100) NOT NULL,
			  enabled BOOLEAN NOT NULL
			);

			CREATE TABLE authorities (
			  username VARCHAR(50) NOT NULL,
			  authority VARCHAR(50) NOT NULL,
			  CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
			);

			CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
	*/
	
	// vous pouvez utiliser le JdbcUserDetailsManager de Spring Security pour gérer les utilisateurs et les rôles;
	// si vous avez un schéma différent, il faut préciser dans les méthodes setAuthoritiesByUsernameQuery et setUsersByUsernameQuery
	// les requêtes SQL pour récupérer les utilisateurs et les rôles.
			
			
//	 @Bean
//	    public UserDetailsService userDetailsService(DataSource dataSource) {
//	    	JdbcUserDetailsManager userDeJdbcUserDetailsManager= new JdbcUserDetailsManager(dataSource);
//	    	userDeJdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, enabled from users where username = ?");
//	    	userDeJdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username, authority from authorities where username = ?");
//	    	return userDeJdbcUserDetailsManager;
//	        
//	    }
}