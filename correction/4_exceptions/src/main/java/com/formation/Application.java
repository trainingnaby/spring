package com.formation;

import java.util.Iterator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.formation.service.DuplicataService;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		
		// liste tous les beans qui sont dans le contexte spring et les affiche
		String [] beans = context.getBeanDefinitionNames();
		for (String beanName : beans) {
			System.out.println(beanName);
		}
		
		// Récupérer le bean de nom duplicataService
		DuplicataService duplicataService = context.getBean("duplicataService", DuplicataService.class);
		System.out.println(duplicataService.listDuplicatas());
		
		
	}

}
