package com.formation.web;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/")
    public Map<String, String> accueil() {
        return Map.of(
            "tp", "TP 3 - Filtre personnalise Spring Security",
            "message", "La page d'accueil est publique. Utilisez X-API-KEY pour les routes /api/**."
        );
    }

    @GetMapping("/api/client")
    public Map<String, Object> client(Principal principal, Authentication authentication) {
        return Map.of(
            "message", "Bienvenue dans l'espace client",
            "utilisateur", principal.getName(),
            "authorities", authentication.getAuthorities()
        );
    }

    @GetMapping("/api/admin")
    public Map<String, Object> admin(Principal principal, Authentication authentication) {
        return Map.of(
            "message", "Bienvenue dans l'espace administration",
            "utilisateur", principal.getName(),
            "authorities", authentication.getAuthorities()
        );
    }
}
