package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.annotationconfig.lazy.LazyClasse;
import basics.configuration.FactoryConfig;

public class Main {
	
	public static void main(String args[]) {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(FactoryConfig.class);
		
		LazyClasse lazy = (LazyClasse) context.getBean("lazyClasse");
		lazy.methodeLazy();
		
//		Train train = (Train) context.getBean("trainBean");
//		train.bouger();
//		Avion avion = (Avion) context.getBean("avionBean");
//		avion.bouger();
//		
//		Bateau bateau = (Bateau) context.getBean("bateau");
//		bateau.bouger();
//		
//		Voiture voiture = (Voiture) context.getBean("ma_voiture");
//		voiture.bouger();
//		
//		Batiment batiment = (Batiment) context.getBean("batiment");
//		batiment.DonneesCoursBatiment();
//		
//		Jeu jeu = (Jeu) context.getBean("mon_jeu");
//		System.out.println(jeu);
//		
//		Etudiant etudiant = (Etudiant) context.getBean("etudiant");
//		System.out.println(etudiant);
//		
//		Pays pays = (Pays) context.getBean("mon_pays");
//		System.out.println(pays);
//		
//		FormationMetadata formationMetadata = (FormationMetadata) context.getBean("formation_spring");
//		formationMetadata.lireMetadataFormation();
//		
//		DonneesCours donneesCours = (DonneesCours) context.getBean("dataCursus");
//		System.out.println(donneesCours);
//		
//		Nid nid = (Nid) context.getBean("nid");
//		nid.donnesNid();
//		
//		System.out.println("===============================");
//		ActeurService acteurService = (ActeurService) context.getBean("acteurService");
//		System.out.println(acteurService.getNomActeur());
//		System.out.println(acteurService.isEmailValide());
//		System.out.println(acteurService.getExempleDate());
//		
//		System.out.println("===============================");
		
		context.close();
	}
	

}
