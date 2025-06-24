package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.formation.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityRules {
	
	  @Autowired
	  CustomUserDetailsService customDatabaseUserDetailsService;

	  
	@Bean
	public SecurityFilterChain definitionRules(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeHttpRequests(auth -> auth
				.requestMatchers("/createuser", "/", "/css/**", "/js/**", "/h2-console/**", "/actuator/**").permitAll()
				.requestMatchers("/genererDuplicata", "/api/duplicata/dto").hasAuthority("ADMIN")
				.anyRequest().authenticated())
		.csrf().disable()
				.formLogin(Customizer.withDefaults());
		
		return httpSecurity.build();
	}
	
	// provider d'authentification : applique une logique d'authentification et essaie d'authentifier le user
	// si il y arrive, le processus d'authentification est terminé; sinon passe au provider suivant ...
	// ce provider DaoAuthenticationProvider contient une logique pour authentifier des users en base
	
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customDatabaseUserDetailsService); 
        auth.setPasswordEncoder(passwordEncoder()); 
        return auth;
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	

}
