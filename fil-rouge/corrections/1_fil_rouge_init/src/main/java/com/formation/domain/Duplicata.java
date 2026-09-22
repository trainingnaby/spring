package com.formation.domain;
// C'est une classe de domaine qui représente une duplicata avec un identifiant, un identifiant d'utilisateur, un montant et une URL de PDF.
// Pas besoin que Spring la gère comme un bean, donc pas d'annotation @Component ou @Service.
public class Duplicata {

	private String id;
	private String userId;
	private Integer montant;
	private String pdfUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	@Override
	public String toString() {
		return "Duplicata [id=" + id + ", userId=" + userId + ", montant=" + montant + ", pdfUrl=" + pdfUrl + "]";
	}

}
