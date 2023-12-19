package com.formation.domain;

public class Joueur {
	
	private String nom;

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@Override
	public String toString() {
		return "Joueur [nom=" + nom + "]";
	}
	
	
}
