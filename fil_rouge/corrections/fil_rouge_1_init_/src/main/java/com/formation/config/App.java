package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {

	public static void main(String[] args) {

		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		// récupération du bean une premiere fois
		DuplicataService duplicataService1 = (DuplicataService) context.getBean("duplicataService");
		System.out.println(duplicataService1);

//		// récupération du bean une deuxieme fois
//		DuplicataService duplicataService2 = (DuplicataService) context.getBean("duplicataService");
//		System.out.println(duplicataService2);
//
//		// récupération du bean une deuxieme fois
//		DuplicataService duplicataService3 = (DuplicataService) context.getBean("duplicataService");
//		System.out.println(duplicataService3);
//
		duplicataService1.createDuplicata("ADERR", 3000);
		duplicataService1.createDuplicata("OIJNH", 7000);
		System.out.println(duplicataService1.getDuplicatas());
	}

}
