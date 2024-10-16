package com.formation.formulaires;

import jakarta.validation.constraints.NotBlank;

public class FormulaireUser {
	
	@NotBlank
	private String numeroFiscal;
	
	@NotBlank
	private String username;
	
	@NotBlank
	private String password;

	public String getNumeroFiscal() {
		return numeroFiscal;
	}

	public void setNumeroFiscal(String numeroFiscal) {
		this.numeroFiscal = numeroFiscal;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
