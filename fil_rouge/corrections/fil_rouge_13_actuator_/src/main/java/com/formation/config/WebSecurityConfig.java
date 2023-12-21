package com.formation.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
		@Autowired
		CustomDatabaseUserDetailsService customDatabaseUserDetailsService;
	
	  // permet l'encodage des mots de passe en base (lors de la création des users
		// et lors du login
		@Bean
		public static PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		// bean pour rechercher les entrées en base de données
		@Bean
		public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
			return customDatabaseUserDetailsService; 
		}
		
		// Déclaration le bean de type DaoAuthenticationProvider (fait le travail effectif)
		@Bean
	    public DaoAuthenticationProvider authenticationProvider(CustomDatabaseUserDetailsService customDBUserDetailsService) {
	        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
	        auth.setUserDetailsService(customDatabaseUserDetailsService); 
	        auth.setPasswordEncoder(passwordEncoder()); 
	        return auth;
	    }
	
	
    // liste des urls sans authentification
	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = {
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/js/**"),
            new AntPathRequestMatcher("/h2-console/**"),
            new AntPathRequestMatcher("/actuator/**"),
    };
	
    // liste des urls avec authentification et role ADMIN
	private static final AntPathRequestMatcher[] URLS_ADMIN = {
            new AntPathRequestMatcher("/editerDuplicata/**"),
    };
	
	private static final AntPathRequestMatcher[] URLS_AUTHENTICATED = {
            new AntPathRequestMatcher("/genererDuplicata/**"),
            new AntPathRequestMatcher("/listeDuplicatas/**")
    };
	
	
	

    //COnfiguration de la SecurityFilterChain de Spring
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return  http.authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST_URLS)
                .permitAll()
                .requestMatchers(URLS_AUTHENTICATED).authenticated()
                .anyRequest().denyAll()
                )
        .headers((headers) -> headers.frameOptions((frame) -> frame.disable())) // necessaire pour H2
		.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
		.cors(cors -> cors.disable())
		.formLogin(
				form -> form
						.loginPage("/seloguer")
						.loginProcessingUrl("/seloguer")
						.defaultSuccessUrl("/")
						.permitAll())
		
		.logout(
				logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/sedeconnecter"))
						.permitAll()
		)
        .build();
	
	}

}