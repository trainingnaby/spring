package com.formation.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component("mon_jeu")
public class Jeu {
	
	// Autowiring par type sur une dépendance => utiliser @Autowired
	// Autowiring en précisant le bean à injecter => utiliser @Resource(name = "nom_du_bean")
	@Resource(name = "le_joueur")
	private Joueur joueur;

	public Joueur getJoueur() {
		return joueur;
	}

	public void setJoueur(Joueur joueur) {
		this.joueur = joueur;
	}

	@Override
	public String toString() {
		return "Jeu [joueur=" + joueur + "]";
	}
	
}
