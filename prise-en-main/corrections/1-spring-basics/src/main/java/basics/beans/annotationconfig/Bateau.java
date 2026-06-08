package basics.beans.annotationconfig;

import org.springframework.stereotype.Component;

import basics.beans.xmlconfig.Vehicule;

@Component// Cette annotation indique que cette classe est un composant Spring
public class Bateau implements Vehicule {

	@Override
	public void bouger() {
		System.out.println("Je bouge sur l'eau");
	}

}
