package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.factoryconfig.Train;
import basics.configuration.FactoryConfig;

public class Main {
	
	public static void main(String args[]) {
//		// crée l'application context à partir du fichier XML de configuration
//		AbstractApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
//		//Avion avion = (Avion) context.getBean("avionBean");
//		Avion avion = context.getBean("avionBean", Avion.class);
//		avion.bouger();
//		//context.close();
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(FactoryConfig.class);
		Train train = context.getBean(Train.class); // Récupération du bean de type Train
		System.out.println("Train bean recuperation 1: " + train);
		Train train2 = context.getBean(Train.class);
		System.out.println("Train bean recuperation 2: " + train2);
		
		// afficher tous les beans du contexte
		String[] beanNames = context.getBeanDefinitionNames();
		System.out.println("Beans dans le contexte :");
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
		
		
		train.bouger();
		context.close();
	}
	

}
