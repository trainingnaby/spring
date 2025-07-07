package basics.beans.xmlconfig.jeu;

public class Etudiant {

	private Addresse adresse;

	public Addresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Addresse adresse) {
		this.adresse = adresse;
	}

	@Override
	public String toString() {
		return "Etudiant [adresse=" + adresse + "]";
	}

}
