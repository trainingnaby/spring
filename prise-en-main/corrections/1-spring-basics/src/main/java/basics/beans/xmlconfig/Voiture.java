package basics.beans.xmlconfig;

public class Voiture {
	
	// La voiture a une dépendance à un moteur
	private Moteur moteur;
	
	private Immatriculation immatriculation;
	
	// Point d'injection du immatriculation : le constructeur
	public Voiture(Immatriculation immatriculation) {
		this.immatriculation = immatriculation;
	}

	// Le moteur est injecté via un setter
	public void setMoteur(Moteur moteur) {
		this.moteur = moteur;
	} 
	
	public void bouger(){
		System.out.println (" Je suis une voiture et ...");
		moteur.rouler();
		immatriculation.origineImmatriculation();
	}
	

}
