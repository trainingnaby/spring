package com.formation.dto;

public class UserDto {
	
	private String numeroFiscal;
	private String username;
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
	@Override
	public String toString() {
		return "UserDto [numeroFiscal=" + numeroFiscal + ", username=" + username + ", password=" + password + "]";
	}
	
	
	

}
