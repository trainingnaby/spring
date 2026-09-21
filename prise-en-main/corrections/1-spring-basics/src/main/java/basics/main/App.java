package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.annotationconfig.Bateau;
import basics.beans.factoryconfig.Batiment;
import basics.beans.factoryconfig.Train;
import basics.beans.xmlconfig.Avion;
import basics.beans.xmlconfig.Voiture;
import basics.beans.xmlconfig.jeu.Etudiant;
import basics.beans.xmlconfig.jeu.Jeu;
import basics.configuration.FactoryConfig;

public class App {

	public static void main(String[] args) {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(FactoryConfig.class);
		
		Train train = (Train) context.getBean("trainBean");
		train.bouger();
		
		Avion avion = (Avion) context.getBean("avionBean");
		avion.bouger();
		
		Bateau bateau = (Bateau) context.getBean("bateau");
		bateau.bouger();
		
		Voiture voiture = (Voiture) context.getBean("ma_voiture");
		voiture.bouger();
		
		Batiment batiment = (Batiment) context.getBean("batiment");
		batiment.donneesBatiment();
		
		Jeu jeu = (Jeu) context.getBean("jeu");
		System.out.println(jeu);
		
		Etudiant etudiant = (Etudiant) context.getBean("etudiant");
		System.out.println(etudiant);
		
//		System.out.println("====================================");
//		for(String bean : context.getBeanDefinitionNames())
//			System.out.println(bean);
//		
//		System.out.println("====================================");
//		
		context.close();

	}
}
