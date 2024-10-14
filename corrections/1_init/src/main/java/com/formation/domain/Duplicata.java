package com.formation.domain;

import java.time.Year;

public class Duplicata {

	private String id;
	private String numeroFiscal;
	private Integer montant;
	private String pdfUrl;
	private Year annee;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumeroFiscal() {
		return numeroFiscal;
	}

	public void setNumeroFiscal(String numeroFiscal) {
		this.numeroFiscal = numeroFiscal;
	}

	public Integer getMontant() {
		return montant;
	}

	public void setMontant(Integer montant) {
		this.montant = montant;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public Year getAnnee() {
		return annee;
	}

	public void setAnnee(Year annee) {
		this.annee = annee;
	}

	@Override
	public String toString() {
		return "Duplicata [id=" + id + ", numeroFiscal=" + numeroFiscal + ", montant=" + montant + ", pdfUrl=" + pdfUrl
				+ ", annee=" + annee + "]";
	}

}
