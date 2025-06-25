package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/api/hello")
    @ResponseBody
    public String apiHello() {
        return "Hello from API (secured by HTTP Basic)";
    }
}
