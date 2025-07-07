package basics.beans.xmlconfig.jeu;

public class Jeu {
	
	private Joueur joueur;

	public Joueur getJoueur() {
		return joueur;
	}

	public void setJoueur(Joueur joueur) {
		this.joueur = joueur;
	}

	@Override
	public String toString() {
		return "Jeu [joueur=" + joueur + "]";
	}

}
