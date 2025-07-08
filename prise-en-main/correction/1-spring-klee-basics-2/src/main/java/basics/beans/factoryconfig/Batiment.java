package basics.beans.factoryconfig;

public class Batiment {
	
	private Constructeur constructeur;
	
	private MaitreOuvrage maitreOuvrage;
	
	public Batiment(MaitreOuvrage maitreOuvrage) {
		this.maitreOuvrage = maitreOuvrage;
	}
	
	public void setConstructeur(Constructeur constructeur) {
		this.constructeur = constructeur;
	}
	
	public void DonneesCoursBatiment(){
		constructeur.marqueConstructeur();
		maitreOuvrage.maitreOuvrage();
	}

}
