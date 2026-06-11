package com.formation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Données nécessaires pour générer un duplicata d'impôts")
public class DuplicataDto {

    @NotBlank
    @JsonProperty("user_id")
    @Schema(description = "Identifiant fiscal de l'utilisateur", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Min(1000)
    @Max(7000)
    @Schema(description = "Montant déclaré pour la génération du duplicata", example = "2500", minimum = "1000", maximum = "7000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer montant;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }
}
