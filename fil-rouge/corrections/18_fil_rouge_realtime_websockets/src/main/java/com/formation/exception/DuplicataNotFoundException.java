package com.formation.exception;

/**
 * Exception fonctionnelle declenchee lorsqu'un duplicata demande n'existe pas.
 */
public class DuplicataNotFoundException extends RuntimeException {

    private final String duplicataId;

    public DuplicataNotFoundException(String duplicataId) {
        super("Duplicata introuvable avec l'identifiant : " + duplicataId);
        this.duplicataId = duplicataId;
    }

    public String getDuplicataId() {
        return duplicataId;
    }
}
