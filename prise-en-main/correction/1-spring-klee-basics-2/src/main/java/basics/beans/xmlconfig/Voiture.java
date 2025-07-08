package basics.beans.xmlconfig;

public class Voiture implements Vehicule {

	private Moteur moteur;
	
	private Immatriculation immatriculation;
	
	// point d'injection de dépendance via constructeur
	public Voiture(Immatriculation immatriculation) {
		this.immatriculation = immatriculation;
	}

	// point d'injection de dépendance via setter
	public void setMoteur(Moteur moteur) {
		this.moteur = moteur;
	}

	public void bouger(){
		System.out.println (" Je suis une voiture et ...");
		moteur.rouler();
		immatriculation.origineImmatriculation();
	}

}
