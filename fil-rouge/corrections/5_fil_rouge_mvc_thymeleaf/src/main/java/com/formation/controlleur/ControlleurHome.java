package com.formation.controlleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControlleurHome  {

    @GetMapping("/")
    public String index() {
        return "redirect:/ui/duplicatas";
    }
}
