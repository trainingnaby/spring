package com.formation.websocket;

import java.time.LocalDateTime;

import com.formation.domain.Duplicata;

public class DuplicataNotification {

    private String type;
    private String message;
    private String duplicataId;
    private String userId;
    private Integer montant;
    private LocalDateTime dateNotification;

    public DuplicataNotification() {
    }

    public DuplicataNotification(String type, String message, String duplicataId, String userId, Integer montant) {
        this.type = type;
        this.message = message;
        this.duplicataId = duplicataId;
        this.userId = userId;
        this.montant = montant;
        this.dateNotification = LocalDateTime.now();
    }

    public static DuplicataNotification creation(Duplicata duplicata) {
        return new DuplicataNotification(
                "CREATION",
                "Un nouveau duplicata a été généré pour l'utilisateur " + duplicata.getUserId(),
                duplicata.getId(),
                duplicata.getUserId(),
                duplicata.getMontant());
    }

    public static DuplicataNotification suppression(Duplicata duplicata) {
        return new DuplicataNotification(
                "SUPPRESSION",
                "Le duplicata " + duplicata.getId() + " a été supprimé.",
                duplicata.getId(),
                duplicata.getUserId(),
                duplicata.getMontant());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDuplicataId() {
        return duplicataId;
    }

    public void setDuplicataId(String duplicataId) {
        this.duplicataId = duplicataId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(LocalDateTime dateNotification) {
        this.dateNotification = dateNotification;
    }
}
