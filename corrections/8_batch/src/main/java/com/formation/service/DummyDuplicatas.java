package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class DummyDuplicatas {
	
	@Autowired // dire à Spring d'injecter la dépendence
	private DuplicataService duplicataService;
	
	// méthode qui sera exécuté aprés la création du bean par Spring
	@PostConstruct
	public void initDuplicatas() {
		
		duplicataService.createDuplicata("AAA", 2000, 2005);
		duplicataService.createDuplicata("BBB", 1500, 2006);
		duplicataService.createDuplicata("CCC", 4000, 2012);
		
	}

}
