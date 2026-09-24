package fr.formation.openapi.dto;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse retournée en cas d'erreur")
public record ApiError(
    @Schema(example = "2026-09-23T08:00:00Z") Instant timestamp,
    @Schema(example = "404") int status,
    @Schema(example = "Employé 99 introuvable") String message
) {}
