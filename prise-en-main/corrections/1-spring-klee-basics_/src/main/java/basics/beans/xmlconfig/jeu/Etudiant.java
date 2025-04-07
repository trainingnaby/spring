package basics.beans.xmlconfig.jeu;

public class Etudiant {
	
	private Addresse addresse;

	public Addresse getAddresse() {
		return addresse;
	}

	public void setAddresse(Addresse addresse) {
		this.addresse = addresse;
	}

	@Override
	public String toString() {
		return "Etudiant [addresse=" + addresse + "]";
	}
	
}
