package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.domain.Duplicata;
import com.formation.service.DuplicataService;

public class App {
	
	public static void main(String[] args) {
		// chargement du contexte
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		// récupération d"un bean du contexte
		DuplicataService duplicataService = (DuplicataService) context.getBean("duplicataService");
		System.out.println(duplicataService);
		
		DuplicataService duplicataService2 = (DuplicataService) context.getBean("duplicataService");
		System.out.println(duplicataService2);
		
		DuplicataService duplicataService3 = (DuplicataService) context.getBean("duplicataService");
		System.out.println(duplicataService3);
		
//		Duplicata duplicata1 = duplicataService.createDuplicata("XXX", 800);
//		
//		System.out.println(duplicata1);
//		
//		Duplicata duplicata2 = duplicataService.createDuplicata("YYY", 1200);
//		
//		System.out.println(duplicata2);
//		
		System.out.println(duplicataService.getDuplicatas());
		
		context.close();
		
	}

}
