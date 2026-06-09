package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.service.DuplicataService;

public class App {
	
	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		DuplicataService duplicatService = context.getBean(DuplicataService.class);
		duplicatService.createDuplicata("user-123", 100);
		System.out.println("Duplicatas: " + duplicatService.getDuplicatas());
	}

}
