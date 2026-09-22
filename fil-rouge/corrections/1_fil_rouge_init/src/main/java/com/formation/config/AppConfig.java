package com.formation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.formation")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
// Récupération dynamique du fichier de configuration en fonction du profil actif (dev, prod)
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
public class AppConfig {

}
