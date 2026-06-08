package basics.beans.xmlconfig;

public class Train implements Vehicule {

	@Override
	public void bouger() {
		System.out.println("Je bouge sur les rails");
	}

}
