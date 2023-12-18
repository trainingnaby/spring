package com.formation.domain;

public class Avion implements Vehicule{

	@Override
	public void bouger(String typeVehicule) {
		System.out.println ("je bouge en "+typeVehicule);
	}

}
