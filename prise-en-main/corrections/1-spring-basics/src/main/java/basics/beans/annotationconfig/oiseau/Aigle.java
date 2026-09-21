package basics.beans.annotationconfig.oiseau;

import org.springframework.stereotype.Component;

@Component("aigle")
public class Aigle implements Oiseau {

	@Override
	public void typeOiseau() {
		System.out.println("Je suis un aigle");
	}
	
	

}
