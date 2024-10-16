package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;

// activer les paramètres de gestion de sécurité

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.formation.filter.JwtFilter;

@EnableWebSecurity
@Configuration
public class DuplicataSecurityFilterChain {
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	JwtFilter jwtFilter;
	
	// urls à whiteslister
    private static final AntPathRequestMatcher []  WEB_WHITE_LIST_URLS = {
                    new AntPathRequestMatcher("/user/create**"),
        			new AntPathRequestMatcher("/user/token**"),
    };
    
    @Bean // ce bean contiendra les règles de sécurité 
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
            
            http.authorizeHttpRequests( authorizeRequests -> { 
                            authorizeRequests.requestMatchers(WEB_WHITE_LIST_URLS).permitAll(); // whitelist les urls
                            authorizeRequests.anyRequest().authenticated(); // authentification pour le reste
            })
            
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           .authenticationManager(authenticationManager())
           .csrf(csrf -> csrf.disable())
           .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
            
    }
    
    
    // permet de hacher les mots de passe avant stockage et lors du login
    @Bean
    public static PasswordEncoder encoder () {
    	return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    	authenticationProvider.setUserDetailsService(customUserDetailsService);
    	authenticationProvider.setPasswordEncoder(encoder());
    	return authenticationProvider;
    }
    
    // forcer spring à définir un AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager() {
    	return new ProviderManager(authenticationProvider());
    }
    
    
}
