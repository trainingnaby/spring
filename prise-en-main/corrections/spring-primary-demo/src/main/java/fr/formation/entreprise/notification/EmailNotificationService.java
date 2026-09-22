package fr.formation.entreprise.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class EmailNotificationService implements NotificationService {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.printf("[EMAIL] Destinataire=%s | %s%n", destinataire, message);
    }
}
