package fr.formation.exceptions.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Aucun client trouvé avec l'identifiant " + id);
    }
}
