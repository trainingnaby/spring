package com.formation.domain;

public class Voiture {
	
	// déclaration de la dépendance
	private Moteur moteur;
	
	// setter => point d'injection
	public void setMoteur(Moteur moteur) {
		this.moteur = moteur;
	}
	
	public void bouger(){
		moteur.rouler();
	}

}
