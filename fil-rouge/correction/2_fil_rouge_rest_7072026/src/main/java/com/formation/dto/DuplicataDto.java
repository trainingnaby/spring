package com.formation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DuplicataDto {

	@JsonProperty("user_id")
	private String userId;

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