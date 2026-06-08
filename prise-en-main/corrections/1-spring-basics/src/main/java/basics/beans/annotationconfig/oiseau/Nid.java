package basics.beans.annotationconfig.oiseau;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Nid {

	@Autowired
	@Qualifier("aigle") // pour lever l'ambiguité entre les deux implémentations de Oiseau
	private Oiseau oiseau;
	

	public void donnnesNid() {
		System.out.println("Un nid pour : ");
		oiseau.typeOiseau();
	}

}
