package com.formation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String numeroFiscal;
	private String username;
	private String password;
	private String authorities; // ADMIN, USER
	private int enabled;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_addresse", referencedColumnName = "id_addresse")
	private Addresse addresse;

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
