package com.formation.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service JWT volontairement simple pour un TP.
 *
 * En production, on preferera une bibliotheque dediee comme Nimbus JOSE + JWT ou
 * jjwt. Ici, l'objectif est pedagogique : montrer la structure d'un JWT signe en
 * HMAC sans ajouter trop de dependances.
 */
@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expirationSeconds;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT");

        Map<String, Object> payload = Map.of(
                "sub", userDetails.getUsername(),
                "roles", roles,
                "iat", now.getEpochSecond(),
                "exp", now.plusSeconds(expirationSeconds).getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedPayload = encodeJson(payload);
        String unsignedToken = encodedHeader + "." + encodedPayload;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public String extractUsername(String token) {
        return payload(token).get("sub").toString();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            if (!isSignatureValid(token)) {
                return false;
            }
            Map<String, Object> payload = payload(token);
            String username = payload.get("sub").toString();
            long expiration = ((Number) payload.get("exp")).longValue();
            return username.equals(userDetails.getUsername()) && Instant.now().getEpochSecond() < expiration;
        } catch (Exception ex) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> extractRoles(String token) {
        Object roles = payload(token).get("roles");
        if (roles instanceof Collection<?>) {
            return ((Collection<?>) roles).stream().map(Object::toString).toList();
        }
        return List.of();
    }

    private boolean isSignatureValid(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        String unsignedToken = parts[0] + "." + parts[1];
        return sign(unsignedToken).equals(parts[2]);
    }

    private Map<String, Object> payload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("JWT invalide");
            }
            byte[] decoded = BASE64_URL_DECODER.decode(parts[1]);
            return objectMapper.readValue(decoded, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("JWT invalide", ex);
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(value);
            return BASE64_URL_ENCODER.encodeToString(json);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de creer le JWT", ex);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return BASE64_URL_ENCODER.encodeToString(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de signer le JWT", ex);
        }
    }
}
