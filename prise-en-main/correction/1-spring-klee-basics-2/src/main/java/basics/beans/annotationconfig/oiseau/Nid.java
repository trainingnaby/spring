package basics.beans.annotationconfig.oiseau;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Nid {
	
	@Autowired
	@Qualifier("pigeon")
	private Oiseau oiseau;

	public Oiseau getOiseau() {
		return oiseau;
	}

	public void setOiseau(Oiseau oiseau) {
		this.oiseau = oiseau;
	}
	
	public void donnesNid() {
		System.out.println("Un nid pour ...");
		oiseau.typeOiseau();
	}

}
