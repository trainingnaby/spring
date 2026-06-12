package com.formation.web;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'erreur standardisée renvoyée par l'API")
public class ApiError {

    @Schema(description = "Date et heure de l'erreur", example = "2026-06-09T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Code HTTP", example = "400")
    private int status;

    @Schema(description = "Libellé court de l'erreur", example = "Bad Request")
    private String error;

    @Schema(description = "Message fonctionnel", example = "Le montant doit être compris entre 1000 et 7000")
    private String message;

    @Schema(description = "Liste détaillée des erreurs de validation")
    private List<String> details;

    public ApiError() {
    }

    public ApiError(int status, String error, String message, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
