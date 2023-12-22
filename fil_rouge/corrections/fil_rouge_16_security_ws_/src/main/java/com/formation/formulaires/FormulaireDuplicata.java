package com.formation.formulaires;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FormulaireDuplicata {

	@NotBlank(message = "{app.field.blank}")
	@Size(min=5, max=10, message = "{app.length.not.ok}")
	private String userId;

	private int montant;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

}
