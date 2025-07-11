package basics.beans.annotationconfig.oiseau;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("pigeon")
public class Pigeon implements Oiseau {
	
	@Override
	public void typeOiseau() {
		System.out.println("Ici un Pigeon");
	}

}
