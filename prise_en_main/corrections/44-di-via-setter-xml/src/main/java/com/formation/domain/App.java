package com.formation.domain;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main(String args[]) {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		Voiture bean = (Voiture) context.getBean("ma_voiture");
		bean.bouger();
		context.close();
	}
}
