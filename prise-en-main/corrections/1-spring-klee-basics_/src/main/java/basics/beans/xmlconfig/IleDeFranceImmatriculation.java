package basics.beans.xmlconfig;

import basics.beans.xmlconfig.Immatriculation;

public class IleDeFranceImmatriculation implements Immatriculation{

	@Override
	public void origineImmatriculation() {
		System.out.println ("je suis immatriculée en ile de France ");
	}

}
