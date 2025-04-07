
package basics.beans.xmlconfig;

public class Voiture {

	private Moteur moteur;
	
	private Immatriculation immatriculation;
	
	// point d'injection de la dépendance de type Immatriculation
	public Voiture(Immatriculation immatriculation) {
		this.immatriculation = immatriculation;
	}

	// point d'injection de la dépendance de type Moteur
	public void setMoteur(Moteur moteur) {
		this.moteur = moteur;
	}
	
	public void bouger(){
		System.out.println (" Je suis une voiture et ...");
		moteur.rouler();
		immatriculation.origineImmatriculation();
	}

	
}
