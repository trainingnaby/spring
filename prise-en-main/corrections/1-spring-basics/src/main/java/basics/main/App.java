package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.annotationconfig.Bateau;
import basics.beans.annotationconfig.acteur.ActeurService;
import basics.beans.annotationconfig.formation.FormationSpring;
import basics.beans.annotationconfig.lieux.Pays;
import basics.beans.annotationconfig.oiseau.Nid;
import basics.beans.annotationconfig.universite.DonneesCours;
import basics.beans.factoryconfig.Batiment;
import basics.beans.xmlconfig.Avion;
import basics.beans.xmlconfig.Train;
import basics.beans.xmlconfig.Voiture;
import basics.beans.xmlconfig.jeu.Etudiant;
import basics.beans.xmlconfig.jeu.Jeu;
import basics.configuration.FactoryConfig;

public class App {
	
	public static void main(String args[]) {
		
		// Création du contexte Spring à partir de la classe de configuration FactoryConfig
		// Le contexte va automatiquement détecter les beans définis dans FactoryConfig et dans le fichier XML importé
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
		
		Jeu jeu = (Jeu) context.getBean("jeu");
		System.out.println(jeu);
		
		Etudiant etudiant = context.getBean("etudiant", Etudiant.class);
		System.out.println(etudiant);
		
		Pays pays = context.getBean("mon_pays", Pays.class);
		System.out.println(pays);
		
		FormationSpring formation = context.getBean("formation_spring", FormationSpring.class);
		formation.lireMetadataFormation();
		
		DonneesCours donneesCours = context.getBean("dataCursus", DonneesCours.class);
		System.out.println(donneesCours);
		
		Nid nid = context.getBean("nid", Nid.class);
		nid.donnnesNid();
		
		System.out.println("------------------------------");
		ActeurService acteurService = context.getBean("acteurService", ActeurService.class);
		System.out.println(acteurService.getNomActeur());
		System.out.println(acteurService.isEmailValide());
		System.out.println(acteurService.getExempleDate());
		System.out.println("------------------------------");
		
		// lister les beans dans le contexte Spring
		String[] beanNames = context.getBeanDefinitionNames();
		System.out.println("Beans dans le contexte Spring :");	
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
		
		context.close();
		
	}

}
