package com.formation.formulaires;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// backing bean
public class FormulaireConnexion {

	@NotBlank
	@Size(min = 5, max = 20)
	private String login, pwd;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}