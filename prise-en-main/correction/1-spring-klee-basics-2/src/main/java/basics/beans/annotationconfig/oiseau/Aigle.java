package basics.beans.annotationconfig.oiseau;

import org.springframework.stereotype.Component;

@Component("aigle")
//@Primary
public class Aigle implements Oiseau{

	@Override
	public void typeOiseau() {
		System.out.println("Ici un Aigle");
	}

}
