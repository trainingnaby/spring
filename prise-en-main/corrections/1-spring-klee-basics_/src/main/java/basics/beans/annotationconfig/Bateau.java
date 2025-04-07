package basics.beans.annotationconfig;

import org.springframework.stereotype.Component;

import basics.beans.xmlconfig.Vehicule;

@Component
public class Bateau implements Vehicule{ // nom bean = bateau; si nom classe = MaSuperbeClasse, nom bean = maSuperbeClasse

	@Override
	public void bouger() {
		System.out.println ("je bouge en bateau");
	}

}
