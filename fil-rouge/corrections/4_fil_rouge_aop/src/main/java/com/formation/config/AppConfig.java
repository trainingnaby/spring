package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "com.formation")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@EnableWebMvc // active la configuration par défaut de spring mvc : les convertisseurs de message, 
			 //les résolveurs de vues, etc.
@EnableAspectJAutoProxy // active la prise en charge de l'AOP basée sur les annotations (ex: @Aspect, @Before, @After, etc.)
public class AppConfig {
	
	@Bean 
	// pour activer la validation des paramètres de méthode (ex: @RequestParam, @PathVariable, etc.)
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
