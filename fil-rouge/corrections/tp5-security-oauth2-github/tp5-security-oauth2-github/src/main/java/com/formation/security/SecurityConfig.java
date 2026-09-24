package com.formation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
 @Bean
 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
   http
     .authorizeHttpRequests(auth -> auth
       .requestMatchers("/", "/error", "/favicon.ico").permitAll()
       .requestMatchers("/profil").authenticated()
       .anyRequest().authenticated())
     .oauth2Login(oauth -> oauth.defaultSuccessUrl("/profil", true))
     .logout(logout -> logout.logoutSuccessUrl("/"));
   return http.build();
 }
}
