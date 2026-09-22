package fr.formation.entreprise.service;

import fr.formation.entreprise.notification.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AlerteService {
    private final NotificationService notificationService;

    public AlerteService(@Qualifier("smsNotificationService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void envoyerAlerte(String telephone) {
        notificationService.envoyer(telephone, "Une intervention est requise sur votre dossier.");
    }
}
