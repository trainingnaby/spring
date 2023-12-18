package com.formation.domain;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	
	public static void main(String args[]) {
		// déclaration du contexte Spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		
		//récupération de beans dans le context
		Avion bean = (Avion) context.getBean("avionBean");
		
		//appel des méthodes du bean
		bean.bouger("avion");
		context.close();
	}
}
