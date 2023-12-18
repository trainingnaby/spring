package com.formation.configuration;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.doamin.Avion;

public class App {

	
	public static void main(String args[]) {
		// déclaration du contexte Spring via config java
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		//récupération de beans dans le context
		Avion bean = (Avion) context.getBean("avion");
		
		//appel des méthodes du bean
		bean.bouger("avion");
		context.close();
	}
}
