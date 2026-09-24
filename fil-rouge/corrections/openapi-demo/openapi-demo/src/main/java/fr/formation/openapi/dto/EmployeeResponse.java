package fr.formation.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Employé retourné par l'API")
public record EmployeeResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "Sophie") String firstName,
    @Schema(example = "Martin") String lastName,
    @Schema(example = "sophie.martin@entreprise.fr") String email,
    @Schema(example = "Informatique") String department
) {}
