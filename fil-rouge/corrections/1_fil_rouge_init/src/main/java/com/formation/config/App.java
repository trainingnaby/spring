package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {
	
	public static void main(String[] args) {
		
		// Création du contexte Spring à partir de la configuration AppConfig
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		DuplicataService duplicaService = context.getBean(DuplicataService.class);
		System.out.println(" Premiere récuperation, addresse mémoire : "+duplicaService);
		duplicaService.createDuplicata("Yves", 3000);
		duplicaService.createDuplicata("Georges", 7000);
		System.out.println(duplicaService.getDuplicatas());
		
		
		DuplicataService duplicaService2 = context.getBean(DuplicataService.class);
		System.out.println(" Deuxieme récuperation, addresse mémoire : "+duplicaService2);
		
		
	}
}
