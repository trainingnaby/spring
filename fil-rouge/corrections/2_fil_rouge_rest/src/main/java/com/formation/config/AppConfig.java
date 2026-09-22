package com.formation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "com.formation")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
// Récupération dynamique du fichier de configuration en fonction du profil actif (dev, prod)
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
@EnableWebMvc // active la configuration Spring MVC pour l'application web
// plusieurs beans de configuration sont activés par cette annotation, comme le gestionnaire de vues, le gestionnaire de ressources statiques, etc.
public class AppConfig {

}
