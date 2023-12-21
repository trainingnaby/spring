package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.domain.Garage;

public class App {

	
	public static void main(String args[]) {
		// déclaration du contexte Spring via config java
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		//récupération de beans dans le context
		Garage garage = (Garage) context.getBean("garage");
		
		garage.afficherVoiture();
		context.close();
	}
}
