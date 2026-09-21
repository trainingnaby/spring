package fr.formation.aop.application;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.formation.aop.config.ConfigurationSpring;
import fr.formation.aop.service.ServiceCompte;

public class ApplicationDemoAop {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext contexte =
                     new AnnotationConfigApplicationContext(ConfigurationSpring.class)) {

            ServiceCompte serviceCompte = contexte.getBean(ServiceCompte.class);

            System.out.println("\n===== 1. Consultation du compte =====");
            serviceCompte.consulterCompte();

            System.out.println("\n===== 2. Dépôt =====");
            serviceCompte.deposer(250.0);

            System.out.println("\n===== 3. Consultation du solde =====");
            double solde = serviceCompte.consulterSolde();
            System.out.println("[APPLICATION] Solde lu par l'application : " + solde);

            System.out.println("\n===== 4. Retrait valide =====");
            serviceCompte.retirer(100.0);

            System.out.println("\n===== 5. Retrait invalide : déclenche @AfterThrowing =====");
            try {
                serviceCompte.retirer(5000.0);
            } catch (Exception e) {
                System.out.println("[APPLICATION] Exception capturée dans le main : " + e.getMessage());
            }

            System.out.println("\n===== 6. Montant invalide : déclenche aussi @AfterThrowing =====");
            try {
                serviceCompte.deposer(-20.0);
            } catch (Exception e) {
                System.out.println("[APPLICATION] Exception capturée dans le main : " + e.getMessage());
            }

            System.out.println("\n===== Fin de la démonstration =====");
        }
    }
}
