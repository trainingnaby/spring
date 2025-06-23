package com.formation.dto;

public class DuplicataDto {

	private String numeroFiscal;

	private int montant;

	private int annee;

	public String getNumeroFiscal() {
		return numeroFiscal;
	}

	public void setNumeroFiscal(String numeroFiscal) {
		this.numeroFiscal = numeroFiscal;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	@Override
	public String toString() {
		return "DuplicataDto [numeroFiscal=" + numeroFiscal + ", montant=" + montant + ", annee=" + annee + "]";
	}

}
