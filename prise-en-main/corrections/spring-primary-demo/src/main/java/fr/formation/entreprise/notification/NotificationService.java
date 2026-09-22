package fr.formation.entreprise.notification;

public interface NotificationService {
    void envoyer(String destinataire, String message);
}
