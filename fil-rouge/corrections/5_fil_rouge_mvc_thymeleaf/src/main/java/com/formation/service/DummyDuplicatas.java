package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;

import jakarta.annotation.PostConstruct;


@Service
public class DummyDuplicatas {
	
	@Autowired
	private DuplicataService duplicataService;
	
	@PostConstruct // cette méthode sera appelée automatiquement par Spring après l'instanciation du bean et l'injection de ses dépendances
	public void init() {
		duplicataService.createDuplicata("Bob", 100);
		duplicataService.createDuplicata("Alice", 200);
		duplicataService.createDuplicata("Charlie", 300);
	}

}
