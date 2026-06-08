package basics.beans.annotationconfig.oiseau;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("pigeon")
//@Primary
public class Pigeon implements Oiseau {

	@Override
	public void typeOiseau() {
		System.out.println("Je suis un pigeon");
	}

}
