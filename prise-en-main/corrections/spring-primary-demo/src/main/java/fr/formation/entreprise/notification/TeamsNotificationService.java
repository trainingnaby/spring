package fr.formation.entreprise.notification;

import org.springframework.stereotype.Service;

@Service
public class TeamsNotificationService implements NotificationService {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.printf("[TEAMS] Canal=%s | %s%n", destinataire, message);
    }
}
