package com.formation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controlleur {

    @GetMapping("/")
    public String home() {
        return "Bienvenue !";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "Bonjour USER !";
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Bonjour ADMIN !";
    }
}
