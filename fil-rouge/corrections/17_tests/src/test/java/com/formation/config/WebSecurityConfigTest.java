package com.formation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// redefinition des règles de sécurité pour whitelister tous les urls lors des tests
@TestConfiguration
public class WebSecurityConfigTest {
	
	@Bean("securityFilterChainTests")
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	   return  http.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll()
				)
		.headers((headers) -> headers.frameOptions((frame) -> frame.disable())) // necessaire pour H2
		.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
		.cors(cors -> cors.disable())
		.build();
	
	}

}