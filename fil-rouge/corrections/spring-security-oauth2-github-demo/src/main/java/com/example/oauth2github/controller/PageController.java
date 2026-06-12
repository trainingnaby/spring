package com.example.oauth2github.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "public";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model,
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        model.addAttribute("name", attributes.getOrDefault("name", attributes.get("login")));
        model.addAttribute("login", attributes.get("login"));
        model.addAttribute("id", attributes.get("id"));
        model.addAttribute("avatarUrl", attributes.get("avatar_url"));
        model.addAttribute("htmlUrl", attributes.get("html_url"));
        model.addAttribute("attributes", attributes);
        model.addAttribute("clientRegistrationId", authorizedClient.getClientRegistration().getRegistrationId());
        model.addAttribute("accessTokenScopes", authorizedClient.getAccessToken().getScopes());

        return "profile";
    }

    @GetMapping("/me")
    @ResponseBody
    public Map<String, Object> me(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        return oauth2User.getAttributes();
    }

    @GetMapping("/token-info")
    @ResponseBody
    public Map<String, Object> tokenInfo(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {
        return Map.of(
            "clientRegistrationId", authorizedClient.getClientRegistration().getRegistrationId(),
            "tokenType", authorizedClient.getAccessToken().getTokenType().getValue(),
            "scopes", authorizedClient.getAccessToken().getScopes(),
            "expiresAt", String.valueOf(authorizedClient.getAccessToken().getExpiresAt()),
            "note", "Par sécurité, la valeur de l'access token n'est pas affichée."
        );
    }
}
