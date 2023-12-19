package com.formation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.formation.FormationMetadata;

public class App {

	
	public static void main(String args[]) {
		// déclaration du contexte Spring via config java
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		//récupération de beans dans le context
		FormationMetadata bean = (FormationMetadata) context.getBean("formationSpring");
		
		//appel des méthodes du bean
		bean.lireMetadata();
		context.close();
	}
}
