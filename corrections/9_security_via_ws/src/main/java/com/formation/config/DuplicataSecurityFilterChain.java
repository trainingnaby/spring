package com.formation.config;

import org.springframework.beans.factory.annotation.Autowired;

// activer les paramètres de gestion de sécurité

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class DuplicataSecurityFilterChain {
	
	@Autowired
	SalesforceAuthenticationProvider salesforceAuthenticationProvider;
	
	// urls à whiteslister
    private static final AntPathRequestMatcher []  WEB_WHITE_LIST_URLS = {
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/user/dto**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/duplicatas/**"),
                    new AntPathRequestMatcher("/user/dto**"),
    };
    
    private static final AntPathRequestMatcher []  WEB_ADMIN_LIST_URLS = {
                    new AntPathRequestMatcher("/api/duplicata/dto**"),
    };
    
    
    @Bean // ce bean contiendra les règles de sécurité 
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
            
            http.authorizeHttpRequests( authorizeRequests -> { 
                            authorizeRequests.requestMatchers(WEB_WHITE_LIST_URLS).permitAll(); // whitelist les urls
                            authorizeRequests.requestMatchers(WEB_ADMIN_LIST_URLS).hasRole("ADMIN"); // urls necessitant le role admin
                            authorizeRequests.anyRequest().authenticated(); // authentification pour le reste
            })
            
            .formLogin(Customizer.withDefaults()) // activer le formulaire de login
            .httpBasic(Customizer.withDefaults()) // activer l'authenfication
            .csrf(httpSecuCsrf -> httpSecuCsrf.disable()) //activerr H2 marche
            .authenticationProvider(salesforceAuthenticationProvider) // on branche le provider custom
            .headers((headers) -> headers.frameOptions((frame) -> frame.disable()));  // uniquement pour H2 marche
            
            return http.build();
            
    }
    
    
}
