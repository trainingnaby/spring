package fr.formation.entreprise.service;

import fr.formation.entreprise.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiffusionService {
    private final List<NotificationService> notificationServices;

    public DiffusionService(List<NotificationService> notificationServices) {
        this.notificationServices = notificationServices;
    }

    public void diffuser(String destinataire) {
        notificationServices.forEach(service ->
                service.envoyer(destinataire, "Maintenance prévue ce soir à 20h."));
    }
}
