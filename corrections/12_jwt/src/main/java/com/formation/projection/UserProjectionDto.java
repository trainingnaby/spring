package com.formation.projection;

import java.util.Objects;

public class UserProjectionDto {

	private String numeroFiscal;
	private String username;
	private String authorities;
	
	public UserProjectionDto(String numeroFiscal, String username, String authorities) {
		this.numeroFiscal = numeroFiscal;
		this.username = username;
		this.authorities = authorities;
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

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "UserProjectionDto [numeroFiscal=" + numeroFiscal + ", username=" + username + ", authorities="
				+ authorities + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorities, numeroFiscal, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserProjectionDto other = (UserProjectionDto) obj;
		return Objects.equals(authorities, other.authorities) && Objects.equals(numeroFiscal, other.numeroFiscal)
				&& Objects.equals(username, other.username);
	}
	
}
