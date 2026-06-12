package com.example.formlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	// permet de définir les règles de sécurité pour les différentes URL de l'application
	// on peut en avoir plusieurs pour différentes parties de l'application, mais ici on en a qu'une 
	// seule qui couvre tout
	// Exemple : si une application a des services REST et un front MVC, 
	// on peut avoir une SecurityFilterChain pour les endpoints REST et une autre pour les pages MVC
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**").permitAll() // whitelist public endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN") // restrict admin endpoints to ADMIN role
                .requestMatchers("/profile", "/user/**").authenticated() // require authentication for profile and user endpoints
                .anyRequest().authenticated()
            )
            // on override le formulaire de login par défaut de Spring Security
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/profile", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
    

    // bean permettant de définir des utilisateurs en mémoire pour les tests 
    // spring va "croiser" les informations de ce bean avec les requêtes d'authentification pour valider les credentials
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("user"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("USER", "ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    // bean permet de chiffrer les mots de passe en utilisant l'algorithme BCrypt, qui est un algorithme de hachage sécurisé pour les mots de passe
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
