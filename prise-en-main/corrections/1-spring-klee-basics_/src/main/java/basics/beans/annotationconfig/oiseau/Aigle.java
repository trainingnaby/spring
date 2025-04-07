package basics.beans.annotationconfig.oiseau;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("aigle")
@Primary // si ambiguité sur une injection de dépendance de type oiseau, injecter en priorité ce bean
public class Aigle implements Oiseau{

	@Override
	public void typeOiseau() {
		System.out.println("Ici un Aigle");
	}

}
