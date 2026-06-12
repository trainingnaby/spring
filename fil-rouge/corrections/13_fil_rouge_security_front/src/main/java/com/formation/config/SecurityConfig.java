package com.formation.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	
	// si on a plusieurs SecurityFilterChain, on peut les différencier avec des @Order pour définir leur priorité.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS est surtout utile pour les appels AJAX depuis un domaine différent.
                // Le front Thymeleaf étant servi par la même application, il n'en a pas besoin,
                // mais on l'active pour montrer le concept et préparer le futur TP REST.
                .cors(Customizer.withDefaults())

                // CSRF reste activé pour le front MVC : les formulaires POST Thymeleaf
                // envoient automatiquement un token CSRF si le champ est présent.
                .csrf(csrf -> csrf
                        // La console H2 utilise des frames et des formulaires internes.
                        // On l'exclut uniquement pour le TP local.
                        .ignoringRequestMatchers("/h2-console/**")
                        // Les routes REST restent volontairement non sécurisées dans ce TP.
                        // Elles seront traitées dans le TP sécurité REST/JWT.
                        .ignoringRequestMatchers("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**", "/cache", "/cache/**"))

                // H2 console : autorise l'affichage dans une frame de même origine.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth
                		
                		// L'ordre des règles est important : les règles plus spécifiques doivent être placées avant les règles plus générales.
                        // Ressources publiques
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/login", "/access-denied").permitAll()

                        // Outils pédagogiques déjà vus : laissés publics pour ne pas bloquer les anciens TP.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // IMPORTANT : la sécurité REST sera traitée plus tard.
                        // On whitelist donc les routes REST existantes.
                        .requestMatchers("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**").permitAll()
                        .requestMatchers("/cache/**").permitAll()

                        // Partie front MVC : création et suppression réservées à ADMIN.
                        // Cette règle est placée avant /ui/duplicatas/* car "new" ressemble à un id.
                        .requestMatchers(HttpMethod.GET, "/ui/duplicatas/new").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/ui/duplicatas", "/ui/duplicatas/*/delete").hasRole("ADMIN")

                        // Partie front MVC : lecture accessible à USER et ADMIN.
                        .requestMatchers(HttpMethod.GET, "/ui/duplicatas", "/ui/duplicatas/*").hasAnyRole("USER", "ADMIN")

                        // Page d'accueil : redirection vers le front sécurisé.
                        .requestMatchers("/").permitAll()

                        // Par défaut, toute autre page nécessite une authentification.
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/ui/duplicatas", true)
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails lecteur = org.springframework.security.core.userdetails.User
                .withUsername("user")
                .password(passwordEncoder.encode("user"))
                .roles("USER")
                .build();

        UserDetails admin = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(lecteur, admin);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // CORS est un protection mis en oeuvre par le navigateur pour empêcher les appels AJAX non autorisés depuis un domaine différent.
    // Le serveur doit explicitement autoriser les domaines frontaux à accéder à ses ressources via des en-têtes CORS.

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5500"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
