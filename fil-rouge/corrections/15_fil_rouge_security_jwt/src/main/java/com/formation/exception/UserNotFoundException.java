package com.formation.exception;

/**
 * Exception fonctionnelle declenchee lorsqu'un utilisateur fiscal n'existe pas.
 */
public class UserNotFoundException extends RuntimeException {

    private final String userId;

    public UserNotFoundException(String userId) {
        super("Utilisateur fiscal introuvable avec l'identifiant : " + userId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
