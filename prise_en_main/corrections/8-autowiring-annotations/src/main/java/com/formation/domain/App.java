package com.formation.domain;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main(String args[]) {
		// déclaration du contexte Spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

		// récupération de beans dans le context
		Jeu bean = (Jeu) context.getBean("mon_jeu");

		System.out.println(bean);
		context.close();
	}
}
