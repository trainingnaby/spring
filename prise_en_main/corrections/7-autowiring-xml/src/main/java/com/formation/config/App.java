package com.formation.config;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.formation.domain.Etudiant;
import com.formation.domain.Jeu;

public class App {

	
	public static void main(String args[]) {	
		// déclaration du contexte Spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		
		//récupération de beans dans le context
		Jeu bean = (Jeu) context.getBean("mon_jeu");
		System.out.println(bean);
		
		Etudiant etudiant = (Etudiant) context.getBean("etudiant");
		System.out.println(etudiant);

		context.close();
	}
}
