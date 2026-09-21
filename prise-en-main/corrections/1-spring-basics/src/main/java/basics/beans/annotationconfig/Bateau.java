package basics.beans.annotationconfig;

import org.springframework.stereotype.Component;

import basics.beans.xmlconfig.Vehicule;

@Component // dit que cette classe devra générer un bean géré par Spring
public class Bateau implements Vehicule{

	@Override
	public void bouger() {
		System.out.println("Je bouge en bateau");
	}

}
