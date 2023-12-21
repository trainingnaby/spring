package com.formation.domain;

import org.springframework.stereotype.Component;

@Component
public class Mercedes implements Voiture {

	@Override
	public String marqueVoiture() {
		return "Je suis une Mercedes";
	}

}
