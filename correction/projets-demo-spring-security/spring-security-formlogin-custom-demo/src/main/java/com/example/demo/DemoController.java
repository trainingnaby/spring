package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to the public HOME page!";
    }

    @GetMapping("/public")
    @ResponseBody
    public String publicEndpoint() {
        return "This is a PUBLIC endpoint!";
    }

    @GetMapping("/private")
    @ResponseBody
    public String privateEndpoint() {
        return "This is a PRIVATE (secured) endpoint!";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
