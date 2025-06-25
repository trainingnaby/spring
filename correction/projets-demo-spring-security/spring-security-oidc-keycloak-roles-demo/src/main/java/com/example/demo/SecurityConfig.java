package com.example.demo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public").permitAll()
                .requestMatchers("/user").hasRole("USER")
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/authenticated").authenticated()
                .anyRequest().authenticated()
        ).oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(this.oidcUserService()))
        );
        return http.build();
    }
    
    private OidcUserService oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = delegate.loadUser(userRequest);
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                try {
                    String accessTokenValue = userRequest.getAccessToken().getTokenValue();
                    JWT jwt = JWTParser.parse(accessTokenValue);
                    Map<String, Object> tokenClaims = jwt.getJWTClaimsSet().getClaims();

                    Map<String, Object> resourceAccess = (Map<String, Object>) tokenClaims.get("resource_access");
                    if (resourceAccess != null) {
                        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("spring-id2");
                        if (clientAccess != null) {
                            List<String> clientRoles = (List<String>) clientAccess.get("roles");
                            for (String role : clientRoles) {
                                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role));
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


    // Utiliser cette méthode si vos roles sont déclarés au niveau realm sur keycloak

	/*
	 * private OidcUserService oidcUserService() { OidcUserService delegate = new
	 * OidcUserService(); return new OidcUserService() {
	 * 
	 * @Override public OidcUser loadUser(OidcUserRequest userRequest) { OidcUser
	 * oidcUser = delegate.loadUser(userRequest); Set<GrantedAuthority>
	 * mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
	 * 
	 * Map<String, Object> claims = oidcUser.getClaims(); Map<String, Object>
	 * realmAccess = (Map<String, Object>) claims.get("realm_access"); if
	 * (realmAccess != null) { List<String> roles = (List<String>)
	 * realmAccess.get("roles"); for (String role : roles) {
	 * mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)); } }
	 * 
	 * return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(),
	 * oidcUser.getUserInfo()); } }; }
	 */
}
