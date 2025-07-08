package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {
	
	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		DuplicataService duplicataService = context.getBean(DuplicataService.class);
		
		duplicataService.createDuplicata("WWWW", 1000);
		System.out.println(duplicataService.getDuplicatas());
	}

}
