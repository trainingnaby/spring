package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the public HOME page!";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a PUBLIC endpoint!";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "This is a PRIVATE (secured) endpoint!";
    }
}
