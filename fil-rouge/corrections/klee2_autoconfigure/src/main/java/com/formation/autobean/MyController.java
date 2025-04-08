package com.formation.autobean;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final ExempleBeanAutoConfiguree beanAutoCOnfigure;
    

    public MyController(ExempleBeanAutoConfiguree helloService) {
        this.beanAutoCOnfigure = helloService;
    }

    @GetMapping("/hello")
    public String hello() {
        return beanAutoCOnfigure.ping();
    }
}