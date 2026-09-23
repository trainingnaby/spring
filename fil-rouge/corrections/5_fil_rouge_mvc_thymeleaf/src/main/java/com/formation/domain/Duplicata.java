package com.formation.domain;


// classe du domaine => ne necessite pas d'annotation @Component, @Service, @Repository, etc.
// c'est une classe simple qui représente une entité métier, elle n'est pas gérée par Spring
public class Duplicata {
	
	
	private String id;
	private String userId;
	private int montant;
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
	public int getMontant() {
		return montant;
	}
	public void setMontant(int montant) {
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
