package fr.formation.corscsrf.api.modele;

public class Compte {
	private String titulaire;
	private double solde;

	public Compte(String titulaire, double solde) {
		this.titulaire = titulaire;
		this.solde = solde;
	}

	public String getTitulaire() {
		return titulaire;
	}

	public double getSolde() {
		return solde;
	}

	public void debiter(double montant) {
		this.solde -= montant;
	}
}
