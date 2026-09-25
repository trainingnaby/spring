package fr.formation.openapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI formationOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("API Gestion des employés")
            .version("1.0")
            .description("API de démonstration utilisée pendant la formation Spring - développer des applications d'entreprise.")
            .contact(new Contact().name("Équipe développement")));
    }
}
