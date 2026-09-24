package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	// AuthenticationManager
	//AuthenticationProvider
	// UserDetailsService
	// PasswordEncoder
	//

    @Bean
    // permet d'encoder les mots de passe des utilisateurs pour les stocker de manière sécurisée.
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    // Référentiel des utilisateurs en mémoire. Il crée deux utilisateurs : "alice" avec le rôle "USER" et "admin" avec les rôles "USER" et "ADMIN". Les mots de passe sont encodés à l'aide du PasswordEncoder défini précédemment.
    // NB :  pour des users en base de données, il faudra créer un
    //UserDetailsService qui interroge la base de données pour récupérer les informations des utilisateurs.
    UserDetailsService users(PasswordEncoder encoder) {
        var alice = User.withUsername("alice")
                .password(encoder.encode("alice123"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(alice, admin);
    }

    @Bean
    // Ce bean represente la configuration de sécurité pour l'application. 
    // Il définit les règles d'accès aux différentes URL, la page de connexion, et la page de déconnexion.	
    
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/espace-client/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/espace-client", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
