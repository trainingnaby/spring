package com.example.demo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "public";
    }

    @GetMapping("/user")
    public String userPage(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("username", oidcUser.getPreferredUsername());
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("username", oidcUser.getPreferredUsername());
        return "admin";
    }

    @GetMapping("/authenticated")
    public String authenticatedPage(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        model.addAttribute("username", oidcUser.getPreferredUsername());
        return "authenticated";
    }
}
