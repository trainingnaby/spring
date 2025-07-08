package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {
	
	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		DuplicataService duplicataService = context.getBean(DuplicataService.class);
		duplicataService.createDuplicata("AAA", 2000);
		duplicataService.createDuplicata("BBB", 3000);
		duplicataService.createDuplicata("CCC", 4000);
		
		System.out.println(duplicataService.getDuplicatas());
	}

}
