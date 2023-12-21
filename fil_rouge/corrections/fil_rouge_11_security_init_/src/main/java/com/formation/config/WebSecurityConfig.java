package com.formation.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
    // liste des urls sans authentification
	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = {
            new AntPathRequestMatcher("/"),
            new AntPathRequestMatcher("/genererDuplicata/**"),
            new AntPathRequestMatcher("/h2-console/**")
    };
	
    // liste des urls avec authentification et role ADMIN
	private static final AntPathRequestMatcher[] URLS_ADMIN = {
            new AntPathRequestMatcher("/listeDuplicatas/**"),
            new AntPathRequestMatcher("/editerDuplicata/**")
    };

    //COnfiguration de la SecurityFilterChain de Spring
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return  http.authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST_URLS)
                .permitAll()
                .requestMatchers(URLS_ADMIN).hasRole("ADMIN")
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