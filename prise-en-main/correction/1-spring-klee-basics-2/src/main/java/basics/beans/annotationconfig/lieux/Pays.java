package basics.beans.annotationconfig.lieux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("mon_pays")
public class Pays {

	@Autowired
	private Ville ville;
	
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
