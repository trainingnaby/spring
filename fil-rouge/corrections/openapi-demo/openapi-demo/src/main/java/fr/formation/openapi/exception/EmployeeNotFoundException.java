package fr.formation.openapi.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id) { super("Employé " + id + " introuvable"); }
}
