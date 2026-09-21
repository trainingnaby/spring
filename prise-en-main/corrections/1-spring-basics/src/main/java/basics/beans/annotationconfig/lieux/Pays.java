package basics.beans.annotationconfig.lieux;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component("mon_pays")
public class Pays {
	
	@Inject
	private Ville ville;
	
	public Pays(Ville ville) {
		this.ville = ville;
	}

	public Ville getVille() {
		return ville;
	}

	public void setVille(Ville ville) {
		this.ville = ville;
	}

	@Override
	public String toString() {
		return "Pays [ville=" + ville + "]";
	}
	
}
