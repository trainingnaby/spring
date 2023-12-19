package com.formation.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.domain.Donnees;

public class App {

	
	public static void main(String args[]) {
		// déclaration du contexte Spring via config java
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		//récupération de beans dans le context
		Donnees bean = (Donnees) context.getBean("data");
		
		//appel des méthodes du bean
		System.out.println(bean);
		context.close();
	}
}
