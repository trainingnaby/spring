package fr.formation.entreprise.service;

import fr.formation.entreprise.notification.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class CommandeService {
    private final NotificationService notificationService;

    public CommandeService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void confirmerCommande(String client) {
        notificationService.envoyer(client, "Votre commande a été validée.");
    }
}
