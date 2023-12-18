package com.formation.doamin;

import org.springframework.stereotype.Component;

@Component
//nom du bean créé = nom de la classe en camelCase
// jeMappelleNaby => jeMappeleNaby

public class Avion implements Vehicule{

	@Override
	public void bouger(String typeVehicule) {
		System.out.println ("je bouge en "+typeVehicule);
	}

}
