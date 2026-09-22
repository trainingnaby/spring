package fr.formation.entreprise.notification;

import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.printf("[SMS] Destinataire=%s | %s%n", destinataire, message);
    }
}
