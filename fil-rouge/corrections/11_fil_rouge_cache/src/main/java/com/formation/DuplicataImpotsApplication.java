package com.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Point d'entrée de l'application Spring Boot.
 *
 * Cette classe remplace :
 * - le lancement manuel de Tomcat,
 * - la création manuelle du WebApplicationContext,
 * - l'enregistrement manuel du DispatcherServlet,
 * - le scan explicite des composants.
 */
@SpringBootApplication
@EnableCaching
public class DuplicataImpotsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuplicataImpotsApplication.class, args);
    }
}
