package com.formation.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/ui/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
