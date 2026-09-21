package basics.beans.xmlconfig.jeu;

public class Etudiant {

	private Addresse addresse;

	public Addresse getAdresse() {
		return addresse;
	}

	public void setAddresse(Addresse adresse) {
		this.addresse = adresse;
	}

	@Override
	public String toString() {
		return "Etudiant [addresse=" + addresse + "]";
	}

}
