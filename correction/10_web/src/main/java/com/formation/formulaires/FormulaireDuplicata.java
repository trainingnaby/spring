package com.formation.formulaires;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FormulaireDuplicata {

	@NotBlank
	@Size(min=2, max=10)
	private String numeroFiscal;

	private int montant;
	
	private int annee;

	public String getNumeroFiscal() {
		return numeroFiscal;
	}

	public void setNumeroFiscal(String numeroFiscal) {
		this.numeroFiscal = numeroFiscal;
	}

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

}
