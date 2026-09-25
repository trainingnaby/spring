package fr.formation.exceptions.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("L'adresse e-mail " + email + " est déjà utilisée");
    }
}
