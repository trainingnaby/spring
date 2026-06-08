package basics.beans.annotationconfig.lieux;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component("mon_pays")
public class Pays {

	// @Autowired // Cette annotation indique à Spring d'injecter automatiquement
	// une instance de Ville dans ce champ
	// @Resource(name = "ma_ville") // Cette annotation indique à Spring d'injecter
	// une instance de Ville avec le nom "ma_ville" dans ce champ
	@Inject // === @Autowired
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
