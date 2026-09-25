package com.formation.exception;

/**
 * Exception fonctionnelle pour les criteres de recherche/pagination non acceptes.
 */
public class InvalidSearchCriteriaException extends RuntimeException {

    public InvalidSearchCriteriaException(String message) {
        super(message);
    }
}
