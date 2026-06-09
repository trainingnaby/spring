package fr.formation.aop.service;

import org.springframework.stereotype.Service;

import fr.formation.aop.domaine.Compte;

@Service // bean de type service, gère la logique métier
public class ServiceCompte {

    private final Compte compte = new Compte("CPT-1001", 1000.0);

    public void deposer(double montant) {
        verifierMontantPositif(montant);

        System.out.println("[METIER] Dépôt de " + montant + " euros");
        compte.ajouter(montant);
    }

    public void retirer(double montant) {
        verifierMontantPositif(montant);

        if (montant > compte.getSolde()) {
            throw new IllegalArgumentException("Solde insuffisant pour retirer " + montant + " euros");
        }

        System.out.println("[METIER] Retrait de " + montant + " euros");
        compte.soustraire(montant);
    }

    public double consulterSolde() {
        System.out.println("[METIER] Consultation du solde");
        return compte.getSolde();
    }

    public Compte consulterCompte() {
        System.out.println("[METIER] Consultation du compte");
        return compte;
    }

    private void verifierMontantPositif(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
    }
}
