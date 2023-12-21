package com.formation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Duplicata{
	
	@Id
	public String id;
	public String userId;
	public Integer montant;
	public String pdfurl;
	
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
	public String getPdfurl() {
		return pdfurl;
	}
	public void setPdfurl(String pdfurl) {
		this.pdfurl = pdfurl;
	}
	@Override
	public String toString() {
		return "Duplicata [id=" + id + ", userId=" + userId + ", montant=" + montant + ", pdfurl=" + pdfurl + "]";
	}

	
}
