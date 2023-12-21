package com.formation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Users {

	@Id
	private String id;
	private String username;
	private String password;
	private String authorities;
	private int enabled;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return "Users [id=" + id + ", username=" + username + ", password=" + password + ", authorities=" + authorities
				+ ", enabled=" + enabled + "]";
	}
	
	
}
