package basics.main;

import java.util.Date;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;

import basics.beans.annotationconfig.Bateau;
import basics.beans.annotationconfig.acteur.ActeurService;
import basics.beans.annotationconfig.formation.FormationMetadata;
import basics.beans.annotationconfig.lieux.Pays;
import basics.beans.annotationconfig.oiseau.Nid;
import basics.beans.annotationconfig.universite.DonneesCours;
import basics.beans.annotationconfig.utility.UserCreatedEvent;
import basics.beans.annotationconfig.utility.UserService;
import basics.beans.factoryconfig.Batiment;
import basics.beans.factoryconfig.Train;
import basics.beans.xmlconfig.Avion;
import basics.beans.xmlconfig.Voiture;
import basics.beans.xmlconfig.jeu.Etudiant;
import basics.beans.xmlconfig.jeu.Jeu;
import basics.configuration.FactoryConfig;

public class App {
	
	public static void main(String args[]) {
		// Lecture de la configuration Java (aussi les configurations dans la classe FactoryConfig)
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
		batiment.DonneesCoursBatiment();
		
		Jeu jeu = (Jeu) context.getBean("mon_jeu");
		System.out.println(jeu);
		
		Etudiant etudiant = (Etudiant) context.getBean("etudiant");
		System.out.println(etudiant);
		
		Pays mon_pays = (Pays) context.getBean("mon_pays");
		System.out.println(mon_pays);
		
		
		FormationMetadata formation = (FormationMetadata) context.getBean("formation_spring");
		formation.lireMetadataFormation();
		
		DonneesCours donneesCours = (DonneesCours) context.getBean("dataCursus");
		System.out.println(donneesCours);
		
		Nid nid = (Nid) context.getBean("nid");
		nid.donnnesNid();
		
		System.out.println("==================================================================");
		ActeurService acteurService= (ActeurService) context.getBean("acteurService");
		System.out.println(acteurService.getNomActeur());
		System.out.println(acteurService.getExempleDate());
		System.out.println(acteurService.isEmailValide());

		System.out.println("==================================================================");

		
		
//		String [] beans =  context.getBeanDefinitionNames();
//		for(String bean : beans) {
//			System.out.println(bean);
//		}
		
		// créer un bean userService et appeler la méthode createUser
		// puis vérifier que l'event listener EmailNotificationListener a bien reçu
		// l'événement UserCreatedEvent
		
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		String[] beans = beanDefinitionNames;
		for (String bean : beans) {
			System.out.println(bean);
		}
		
		UserService userService = context.getBean("userService", UserService.class);
		System.out.println("iiiiiiiiii Prefix "+userService.getPrefix());
		userService.createUser("tata@gmail.com");

		context.publishEvent(new UserCreatedEvent("rrrrr@hotmail.fr"));

		System.out.println("count total beans : " + context.getBeanDefinitionCount());
		System.out.println("list beans : " + beanDefinitionNames);
		context.getBeanDefinitionNames();

		System.out.println("count total beans : " + context.getBeanDefinitionCount());
		System.out.println("list beans : ");
		for (String beanName : beanDefinitionNames) {
			System.out.println(beanName);
		}

		Map<String, UserService> publishers = context.getBeansOfType(UserService.class);
		publishers.forEach((name, bean) -> System.out.println(name + " -> " + bean.getClass().getName()));
		//afifcher attribut prefix
		System.out.println("prefix : " + userService.getPrefix());
		System.out.println(context.isSingleton("userService"));
		
		System.out.println("Contexte démarré le : " + new Date(context.getStartupDate()));
		
		Environment env = context.getEnvironment();
		String javaVersion = env.getProperty("java.version");
		System.out.println("Java version : " + javaVersion);
		
		context.close();
	}

}
