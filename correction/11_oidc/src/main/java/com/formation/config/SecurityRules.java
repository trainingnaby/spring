package com.formation.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityRules {
	

	  
	@Bean
	public SecurityFilterChain definitionRules(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeHttpRequests(auth -> auth
				.requestMatchers("/createuser", "/", "/css/**", "/js/**", "/h2-console/**", "/actuator/**").permitAll()
				.requestMatchers("/genererDuplicata", "/api/duplicata/dto").hasRole("ADMIN")
				.anyRequest().authenticated())
		
		.oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/"));
		
		return httpSecurity.build();
	}
	
	// parsing du token keycloak pour extraire les roles du user
	 @Bean
     public OidcUserService oidcUserService() {
         OidcUserService delegate = new OidcUserService();

         return new OidcUserService() {
             @Override
             public OidcUser loadUser(OidcUserRequest userRequest) {
                 OidcUser oidcUser = delegate.loadUser(userRequest);

                 Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                 String accessTokenValue = userRequest.getAccessToken().getTokenValue();

                 com.nimbusds.jwt.JWT jwt = null;
                 try {
                     jwt = com.nimbusds.jwt.JWTParser.parse(accessTokenValue);
                     Map<String, Object> tokenClaims = jwt.getJWTClaimsSet().getClaims();

                     Map<String, Object> resourceAccess = (Map<String, Object>) tokenClaims.get("resource_access");
                     if (resourceAccess != null) {
                         String clientIdForRoles = userRequest.getClientRegistration().getClientId();
                         Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientIdForRoles);
                         if (clientAccess != null) {
                             List<String> clientRoles = (List<String>) clientAccess.get("roles");
                             if (clientRoles != null) {
                                 for (String role : clientRoles) {
                                     mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role));
                                 }
                             }
                         }
                     }
                 } catch (Exception e) {
                     throw new RuntimeException("Failed to parse access token", e);
                 }

                 return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
             }
         };
     }
	

}
