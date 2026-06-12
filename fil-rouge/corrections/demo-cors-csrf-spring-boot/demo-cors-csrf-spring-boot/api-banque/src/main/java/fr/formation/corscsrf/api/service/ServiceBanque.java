package fr.formation.corscsrf.api.service;

import org.springframework.stereotype.Service;
import fr.formation.corscsrf.api.modele.Compte;

@Service
public class ServiceBanque {
	private final Compte compte = new Compte("Alice", 1000.0);

	public Compte consulterCompte() {
		return compte;
	}

	public String faireVirement(String beneficiaire, double montant) {
		if (montant <= 0)
			throw new IllegalArgumentException("Le montant doit être positif");
		if (montant > compte.getSolde())
			throw new IllegalArgumentException("Solde insuffisant");
		compte.debiter(montant);
		return "Virement de " + montant + " euros vers " + beneficiaire + " effectué. Nouveau solde : "
				+ compte.getSolde();
	}
}
