package com.formation.config;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.security.JwtAuthenticationFilter;

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
            AccessDeniedHandler restAccessDeniedHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        return http
                .securityMatcher("/api/auth/**", "/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**", "/api/cache", "/api/cache/**")
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Endpoint public pour obtenir un token JWT.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

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
                        .ignoringRequestMatchers("/h2-console/**", "/ws-duplicatas/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/webjars/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/login", "/access-denied").permitAll()
                        .requestMatchers("/ws-duplicatas/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

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
                .oauth2Login(oauth2 -> oauth2
                        // On conserve la page de login Thymeleaf existante et on y ajoute
                        // simplement un lien vers /oauth2/authorization/github.
                        .loginPage("/login")
                        .defaultSuccessUrl("/ui/duplicatas", true))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
                .build();
    }



    /**
     * Mapping pedagogique des roles OAuth2.
     *
     * Par defaut, un utilisateur connecte via GitHub possede surtout l'autorite
     * OAUTH2_USER. Pour simplifier le TP, on ajoute ROLE_USER afin qu'il puisse
     * consulter les pages front. Les operations ADMIN restent reservees au compte
     * local admin/admin, sauf si vous enrichissez ce mapper avec une liste de logins
     * GitHub administrateurs.
     */
    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            for (GrantedAuthority authority : authorities) {
                mappedAuthorities.add(authority);
                if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                    Object login = oauth2UserAuthority.getAttributes().get("login");
                    if ("admin".equals(login)) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                }
            }
            return mappedAuthorities;
        };
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
            response.setHeader("WWW-Authenticate", "Bearer realm=\"duplicata-api\"");
            writeProblemDetail(objectMapper, request, response,
                    HttpStatus.UNAUTHORIZED,
                    "Authentification requise",
                    "Vous devez fournir un token JWT valide dans l'en-tete Authorization: Bearer <token>.");
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
