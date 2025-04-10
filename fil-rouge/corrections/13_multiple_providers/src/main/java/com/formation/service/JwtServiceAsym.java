package com.formation.service;


import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtServiceAsym {
	
	private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(20);

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public JwtServiceAsym(@Value("${jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    public String generateToken(final String username, final List<String> roles) {
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(username)
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
                .withClaim("roles", roles) // 👈 Ajouter les rôles ici
                .sign(this.hmac512);
    }

    public String validateTokenAndGetUsername(final String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            System.out.println("token invalid: " + verificationEx.getMessage());
            return null;
        }
    }
    
    public List<String> getRoles(String token) {
        try {
            return verifier.verify(token).getClaim("roles").asList(String.class);
        } catch (Exception e) {
            return List.of(); 
        }
    }



}
