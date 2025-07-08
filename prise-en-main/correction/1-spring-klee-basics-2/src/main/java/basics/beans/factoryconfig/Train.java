package basics.beans.factoryconfig;

import basics.beans.xmlconfig.Vehicule;

public class Train implements Vehicule {

	@Override
	public void bouger() {
		System.out.println("je bouge en train");
	}

}
