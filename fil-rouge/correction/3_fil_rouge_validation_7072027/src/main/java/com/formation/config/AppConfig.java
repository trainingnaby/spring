package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.formation")
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:application-${spring.profiles.active}.properties"}, ignoreResourceNotFound = true)
@EnableWebMvc
public class AppConfig {
	
	@Bean 
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
