package com.formation.web.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.formation.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoint simple pour obtenir un token JWT")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationSeconds;

    public AuthController(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationSeconds = expirationSeconds;
    }

    @Operation(summary = "Se connecter et obtenir un JWT",
            description = "Authentifie un utilisateur en memoire et retourne un token Bearer a utiliser sur les endpoints REST.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(name = "Compte lecteur", value = "{\n  \"username\": \"user\",\n  \"password\": \"user\"\n}"),
                                    @ExampleObject(name = "Compte administrateur", value = "{\n  \"username\": \"admin\",\n  \"password\": \"admin\"\n}")
                            })))
    @ApiResponse(responseCode = "200", description = "Token genere")
    @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        String token = jwtService.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new LoginResponse("Bearer", token, expirationSeconds, userDetails.getUsername(), roles);
    }
}
