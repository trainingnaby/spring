package fr.formation.aop.domaine;

public class Compte {

    private final String numero;
    private double solde;

    public Compte(String numero, double soldeInitial) {
        this.numero = numero;
        this.solde = soldeInitial;
    }

    public String getNumero() {
        return numero;
    }

    public double getSolde() {
        return solde;
    }

    public void ajouter(double montant) {
        this.solde += montant;
    }

    public void soustraire(double montant) {
        this.solde -= montant;
    }

    @Override
    public String toString() {
        return "Compte{numero='" + numero + "', solde=" + solde + "}";
    }
}
