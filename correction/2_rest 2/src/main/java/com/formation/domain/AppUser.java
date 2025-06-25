package com.formation.domain;

public class AppUser {

	private String id;
	private String numeroFiscal;
	private String username;
	private String password;
	private String authorities;
	private int enabled;
	private Addresse addresse;

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

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public Addresse getAddresse() {
		return addresse;
	}

	public void setAddresse(Addresse addresse) {
		this.addresse = addresse;
	}

	@Override
	public String toString() {
		return "AppUser [id=" + id + ", numeroFiscal=" + numeroFiscal + ", username=" + username + ", password="
				+ password + ", authorities=" + authorities + ", enabled=" + enabled + ", addresse=" + addresse + "]";
	}

}
