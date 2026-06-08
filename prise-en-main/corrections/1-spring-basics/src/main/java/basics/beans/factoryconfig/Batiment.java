package basics.beans.factoryconfig;

public class Batiment {

	private Constructeur constructeur;

	private MaitreOuvrage maitreOuvrage;

	// setter pour le constructeur du batiment
	public void setConstructeur(Constructeur constructeur) {
		this.constructeur = constructeur;
	}

	// on passe par le constructeur pour injecter le maitre d'ouvrage du batiment
	public Batiment(MaitreOuvrage maitreOuvrage) {
		this.maitreOuvrage = maitreOuvrage;
	}

	public void DonneesCoursBatiment() {
		constructeur.marqueConstructeur();
		maitreOuvrage.maitreOuvrage();
	}

}
