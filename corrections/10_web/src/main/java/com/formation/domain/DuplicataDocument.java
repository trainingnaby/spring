package com.formation.domain;

import java.time.Year;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "duplicatas") 
public class DuplicataDocument {

	@Id
	private long id;
	private String numeroFiscal;
	private Integer montant;
	private String pdfUrl;
	private int annee;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	@Override
	public String toString() {
		return "DuplicataDocument [id=" + id + ", numeroFiscal=" + numeroFiscal + ", montant=" + montant + ", pdfUrl="
				+ pdfUrl + ", annee=" + annee + "]";
	}

}
