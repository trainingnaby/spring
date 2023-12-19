package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {

	public static void main(String[] args) {

		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		// récupération de beans dans le context
		DuplicataService duplicataService = (DuplicataService) context.getBean("duplicataService");
		
		duplicataService.createDuplicata("ADERR", 3000);
		duplicataService.createDuplicata("OIJNH", 7000);
		System.out.println(duplicataService.getDuplicatas());
	}

}
