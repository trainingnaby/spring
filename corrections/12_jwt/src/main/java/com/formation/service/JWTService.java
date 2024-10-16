package com.formation.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JWTService {

	private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(20);

	private final Algorithm rsa256;
	private final JWTVerifier verifier;

	// permettre de garder la clé de génération
	public JWTService(@Value("classpath:certs/public.pem") final RSAPublicKey publicKey,
			@Value("classpath:certs/private.pem") final RSAPrivateKey privateKey) {
		this.rsa256 = Algorithm.RSA256(publicKey, privateKey);
		this.verifier = JWT.require(this.rsa256).build();
	}

	public String generateToken(final String username) {
		final Instant now = Instant.now();
		return JWT.create().withSubject(username).withIssuer("app").withIssuedAt(now)
				.withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis())).sign(this.rsa256);
	}

	public String validateTokenAndGetUsername(final String token) {
		try {
			return verifier.verify(token).getSubject();
		} catch (final JWTVerificationException verificationEx) {
			System.out.println("token invalid: " + verificationEx.getMessage());
			return null;
		}
	}

}