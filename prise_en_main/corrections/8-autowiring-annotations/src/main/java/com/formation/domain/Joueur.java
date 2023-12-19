package com.formation.domain;

import org.springframework.stereotype.Component;

@Component("le_joueur")
public class Joueur {
	
	private String nom = "Naby";	

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
