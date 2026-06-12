package com.formation.config;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Chaine de securite dediee aux endpoints REST.
     *
     * Elle est separee de la chaine MVC afin d'eviter les redirections vers la page
     * de login HTML. Pour une API REST, on veut plutot des statuts HTTP 401/403 et
     * des reponses JSON au format ProblemDetail.
     */
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint restAuthenticationEntryPoint,
            AccessDeniedHandler restAccessDeniedHandler) throws Exception {

        return http
                .securityMatcher("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**", "/api/cache", "/api/cache/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // pas de CSRF pour les API REST (stateless), pas de formulaires html
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // pas de sessions HTTP pour les API REST
                .formLogin(AbstractHttpConfigurer::disable) // pas de formulaire de login HTML pour les API REST
                .logout(AbstractHttpConfigurer::disable) // pas de logout pour les API REST (on suppose que le client se debrouille pour oublier les credentials)
                .httpBasic(Customizer.withDefaults()) // authentification HTTP Basic pour les clients REST
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Consultation REST : USER ou ADMIN.
                        .requestMatchers(HttpMethod.GET, "/duplicatas", "/duplicatas/**").hasAnyRole("USER", "ADMIN")

                        // Creation et suppression REST : ADMIN uniquement.
                        .requestMatchers(HttpMethod.POST, "/duplicatas", "/duplicatas/**", "/duplicatas_dto").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/duplicatas/**").hasRole("ADMIN")

                        // Endpoints techniques de cache : ADMIN uniquement.
                        .requestMatchers("/api/cache", "/api/cache/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .build();
    }

    /**
     * Chaine de securite pour les pages MVC/Thymeleaf.
     *
     * Cette chaine conserve le fonctionnement du TP precedent : formulaire de login,
     * sessions HTTP et protection CSRF des formulaires HTML.
     */
    @Bean
    @Order(2)
    SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        // La console H2 utilise des frames et des formulaires internes.
                        // On l'exclut uniquement pour le TP local.
                        .ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/login", "/access-denied").permitAll()

                        // Outils pedagogiques. Swagger UI reste public, mais les appels REST
                        // effectues depuis Swagger devront etre authentifies.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Partie front MVC : creation et suppression reservees a ADMIN.
                        .requestMatchers(HttpMethod.GET, "/ui/duplicatas/new").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/ui/duplicatas", "/ui/duplicatas/*/delete").hasRole("ADMIN")

                        // Partie front MVC : lecture accessible a USER et ADMIN.
                        .requestMatchers(HttpMethod.GET, "/ui/duplicatas", "/ui/duplicatas/*").hasAnyRole("USER", "ADMIN")

                        // Page d'accueil : redirection vers le front securise.
                        .requestMatchers("/").permitAll()

                        // Par defaut, toute autre page necessite une authentification.
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5500"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("WWW-Authenticate"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> {
            response.setHeader("WWW-Authenticate", "Basic realm=\"duplicata-api\"");
            writeProblemDetail(objectMapper, request, response,
                    HttpStatus.UNAUTHORIZED,
                    "Authentification requise",
                    "Vous devez fournir un identifiant et un mot de passe valides pour appeler cette API REST.");
        };
    }

    @Bean
    AccessDeniedHandler restAccessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> writeProblemDetail(objectMapper, request, response,
                HttpStatus.FORBIDDEN,
                "Acces refuse",
                "Votre compte est authentifie, mais il ne possede pas les droits suffisants pour cette operation.");
    }

    private static void writeProblemDetail(
            ObjectMapper objectMapper,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String title,
            String detail) throws IOException, ServletException {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://formation.spring/erreurs/securite"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("method", request.getMethod());
        problem.setProperty("path", request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
