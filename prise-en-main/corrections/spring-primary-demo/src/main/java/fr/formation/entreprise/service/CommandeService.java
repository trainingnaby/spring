package fr.formation.entreprise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import fr.formation.entreprise.notification.NotificationService;

@Service
public class CommandeService {
    private final NotificationService notificationService;
    
    @Autowired
    @Qualifier("smsNotificationService")
    private NotificationService smsNotificationService;

    public CommandeService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void confirmerCommande(String client) {
        notificationService.envoyer(client, "Votre commande a été validée.");
        smsNotificationService.envoyer(client, "Votre commande a été validée par SMS.");
    }
}
