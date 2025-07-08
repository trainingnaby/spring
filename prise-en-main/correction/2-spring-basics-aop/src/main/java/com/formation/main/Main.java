package com.formation.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.config.AppConfig;
import com.formation.service.EnvoiService;

public class Main {

	public static void main(String[] args) {

		// chargement du contexte
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		EnvoiService emailService = (EnvoiService) context.getBean("emailService");

		emailService.envoyer("mon message");


	}

}
