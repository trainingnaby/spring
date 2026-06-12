package fr.formation.corscsrf.api.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
public class SecurityConfig {
	@Value("${app.security.mode}")
	private String mode;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/css/**").permitAll().anyRequest().authenticated());
		http.formLogin(Customizer.withDefaults());
		http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

		if ("insecure".equalsIgnoreCase(mode)) {
			http.csrf(csrf -> csrf.disable());
			http.cors(Customizer.withDefaults());
		} else if ("secure".equalsIgnoreCase(mode)) {
			http.cors(Customizer.withDefaults());
			// CSRF reste activé par défaut.
		}
		// mode csrf-only : CSRF activé, CORS non configuré.
		return http.build();
	}

	@Bean
	UserDetailsService users() {
		UserDetails alice = User.withUsername("alice").password("password").roles("USER").build();
		return new InMemoryUserDetailsManager(alice);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		if ("insecure".equalsIgnoreCase(mode)) {
			configuration.setAllowedOrigins(List.of("http://localhost:9090"));
			configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			configuration.setAllowedHeaders(List.of("*"));
			configuration.setAllowCredentials(true);
		} else if ("secure".equalsIgnoreCase(mode)) {
			configuration.setAllowedOrigins(List.of("http://localhost:4200"));
			configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
			configuration.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));
			configuration.setAllowCredentials(true);
		}
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		if ("insecure".equalsIgnoreCase(mode) || "secure".equalsIgnoreCase(mode)) {
			source.registerCorsConfiguration("/**", configuration);
		}
		return source;
	}
}
