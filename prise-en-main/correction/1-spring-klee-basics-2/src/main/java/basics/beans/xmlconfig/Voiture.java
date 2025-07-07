package basics.beans.xmlconfig;

public class Voiture implements Vehicule {

	private Moteur moteur;

	// point d'injection de dépendance
	public void setMoteur(Moteur moteur) {
		this.moteur = moteur;
	}

	public void bouger() {
		System.out.println(" Je suis une voiture et ...");
		moteur.rouler();
	}

	public Moteur getMoteur() {
		return moteur;
	}

}
