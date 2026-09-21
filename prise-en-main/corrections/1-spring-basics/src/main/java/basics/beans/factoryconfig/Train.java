package basics.beans.factoryconfig;

import basics.beans.xmlconfig.Vehicule;

public class Train implements Vehicule{

	@Override
	public void bouger() {
		System.out.println("Je bouge en train");
	}

}
