package com.formation.web;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccueilController {

    @GetMapping("/")
    public Map<String, Object> accueil() {
        return Map.of(
                "application", "TP 2 - Spring Security HTTP Basic",
                "message", "L'endpoint public fonctionne sans authentification.");
    }

    @GetMapping("/api/client")
    public Map<String, Object> espaceClient(Principal principal) {
        return Map.of(
                "message", "Bienvenue dans l'espace client",
                "utilisateur", principal.getName());
    }

    @GetMapping("/api/admin")
    public Map<String, Object> espaceAdmin(Principal principal) {
        return Map.of(
                "message", "Bienvenue dans l'espace administration",
                "utilisateur", principal.getName());
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("username", authentication.getName());
        resultat.put("authorities", authentication.getAuthorities());
        resultat.put("authenticated", authentication.isAuthenticated());
        resultat.put("authenticationType", authentication.getClass().getSimpleName());
        return resultat;
    }
}
