package basics.beans.annotationconfig;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import basics.beans.xmlconfig.Vehicule;

@Component // bean plus generique
@Lazy
public class Bateau implements Vehicule {

	@Override
	public void bouger() {
		System.out.println("je bouge en bateau");
	}

}
