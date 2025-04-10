package com.formation.filter;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomHeaderAuthenticationProvider implements AuthenticationProvider {

    private static final String LAISSEZ_PASSER_ADMIN = "YO!";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        if (LAISSEZ_PASSER_ADMIN.equals(token)) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new CustomHeaderAuthenticationToken(token, authorities);
        }

        throw new BadCredentialsException("Tu ne passeras pas ! ");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomHeaderAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
