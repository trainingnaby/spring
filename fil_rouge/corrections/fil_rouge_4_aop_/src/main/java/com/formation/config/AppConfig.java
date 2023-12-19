package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan("com.formation")
@PropertySource(value = { "classpath:application.properties" },ignoreResourceNotFound = true)
@EnableWebMvc
@EnableAspectJAutoProxy
public class AppConfig {
	
	@Bean // en charge des conversions java <=>json
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean 
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
