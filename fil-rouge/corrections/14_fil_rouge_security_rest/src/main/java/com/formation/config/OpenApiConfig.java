package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration OpenAPI minimale.
 *
 * Springdoc pourrait déjà générer une documentation sans cette classe.
 * On l'ajoute pour montrer aux stagiaires comment personnaliser le titre,
 * la description, la version et l'URL serveur affichés dans Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI duplicataOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Authentification HTTP Basic pour les endpoints REST du TP.")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Serveur local de formation"))
                .info(new Info()
                        .title("API Duplicatas d'impôts")
                        .version("1.0.0")
                        .description("Documentation OpenAPI de l'application fil rouge de génération de duplicatas d'impôts.")
                        .contact(new Contact()
                                .name("Formation Spring")
                                .email("formation@example.com"))
                        .license(new License()
                                .name("Usage pédagogique")));
    }
}
