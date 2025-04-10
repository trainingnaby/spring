package com.formation.filter;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomHeaderAuthenticationToken extends AbstractAuthenticationToken {

    private final String headerValue;

    public CustomHeaderAuthenticationToken(String headerValue) {
        super(null);
        this.headerValue = headerValue;
        setAuthenticated(false);
    }

    public CustomHeaderAuthenticationToken(String headerValue, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.headerValue = headerValue;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return headerValue;
    }

    @Override
    public Object getPrincipal() {
        return headerValue;
    }
}
