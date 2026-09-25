package com.formation.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityPageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/ui/duplicatas";
    }

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "security/access-denied";
    }
}
