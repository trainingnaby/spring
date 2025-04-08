package com.formation.components;

import com.formation.interfaces.BeanACreer;
import com.formation.interfaces.DoitEtrePoli;

@BeanACreer
public class Train implements Vehicule {
	
	@DoitEtrePoli
	@Override
	public void bouger() {
		System.out.println("Le train roule sur les rails");
	}

}
