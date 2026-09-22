package fr.formation.entreprise;

import fr.formation.entreprise.config.AppConfig;
import fr.formation.entreprise.service.AlerteService;
import fr.formation.entreprise.service.CommandeService;
import fr.formation.entreprise.service.DiffusionService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            System.out.println("\n1 - Injection sans précision : @Primary est choisi");
            context.getBean(CommandeService.class).confirmerCommande("client@entreprise.fr");

            System.out.println("\n2 - Injection avec @Qualifier : le bean demandé est choisi");
            context.getBean(AlerteService.class).envoyerAlerte("06 00 00 00 00");

            System.out.println("\n3 - Injection d'une List : tous les beans restent disponibles");
            context.getBean(DiffusionService.class).diffuser("collaborateurs");
        }
    }
}
