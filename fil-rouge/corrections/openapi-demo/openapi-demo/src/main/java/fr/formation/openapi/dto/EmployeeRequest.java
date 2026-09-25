package fr.formation.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Données nécessaires pour créer ou modifier un employé")
public record EmployeeRequest(
    @Schema(example = "Sophie") @NotBlank String firstName,
    @Schema(example = "Martin") @NotBlank String lastName,
    @Schema(example = "sophie.martin@entreprise.fr") @NotBlank @Email String email,
    @Schema(example = "Informatique") @NotBlank String department
) {}
