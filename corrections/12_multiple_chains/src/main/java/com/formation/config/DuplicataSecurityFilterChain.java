package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;

// activer les paramètres de gestion de sécurité

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
	
	private static final AntPathRequestMatcher[] H2_URLS = {
			new AntPathRequestMatcher("/h2-console/**"),
	};
	
	private static final AntPathRequestMatcher[] WEB_WHITE_LIST_URLS = {
			new AntPathRequestMatcher("/"),
			//new AntPathRequestMatcher("/h2-console/**"),
			new AntPathRequestMatcher("/error"),
			new AntPathRequestMatcher("/login**"),
			new AntPathRequestMatcher("/css/**"),
			new AntPathRequestMatcher("/js/**"),
			new AntPathRequestMatcher("/user/create**"),
			new AntPathRequestMatcher("/user/token**"),
	};
	
	private static final AntPathRequestMatcher[] REST_URLS_WHITELIST = {
			new AntPathRequestMatcher("/user/create**"),
			new AntPathRequestMatcher("/user/token**"),
	};
	
	private static final AntPathRequestMatcher[] WEB_URLS_ADMIN = {
			new AntPathRequestMatcher("/genererDuplicata**"),
	};
	
    
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/api/**")) // pour tous les services rest
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                	auth.requestMatchers(REST_URLS_WHITELIST).permitAll();
                	auth.anyRequest().authenticated();
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    @Order(2)
    SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
		 return http
		            .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
		            .authorizeHttpRequests( auth -> {
		                auth.requestMatchers(H2_URLS).permitAll();
		            })
		            .csrf(csrf -> csrf.ignoringRequestMatchers(H2_URLS))
		            .headers((headers) -> headers.frameOptions((frame) -> frame.disable()))
		            .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> {
                	requests.requestMatchers(WEB_WHITE_LIST_URLS).permitAll();
                	requests.requestMatchers(WEB_URLS_ADMIN).hasRole("ADMIN");
                    requests.anyRequest().authenticated();
                 })
                .authenticationManager(authenticationManager())
                .formLogin(Customizer.withDefaults())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
	
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
    
    @Bean
    public AuthenticationManager authenticationManager() {
    	return new ProviderManager(authenticationProvider());
    }
    
}
