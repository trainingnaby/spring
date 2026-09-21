package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.annotationconfig.Bateau;
import basics.beans.annotationconfig.acteur.ActeurService;
import basics.beans.annotationconfig.formation.FormationMetadata;
import basics.beans.annotationconfig.lieux.Pays;
import basics.beans.annotationconfig.oiseau.Nid;
import basics.beans.annotationconfig.universite.DonneesCours;
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
		
		Pays pays = (Pays) context.getBean("mon_pays");
		System.out.println(pays);
		
		FormationMetadata formationMetadata = (FormationMetadata) context.getBean("formation_spring");
		formationMetadata.lireMetadataFormation();
		
		DonneesCours donneesCours = (DonneesCours) context.getBean("dataCursus");
		System.out.println(donneesCours);
		
		Nid nid = (Nid) context.getBean("nid");
		nid.donnnesNid();
		
		System.out.println("================================");
		ActeurService acteurService = (ActeurService) context.getBean("acteurService");
		System.out.println("Nom en majuscules " + acteurService.getNomActeur());
		System.out.println("Genre Valide : " + acteurService.isGenreValide());
		System.out.println("Nom Valide : " + acteurService.isNomValide());
		System.out.println("Email Valide : " + acteurService.isEmailValide());
		System.out.println("Exemple Data : " + acteurService.getExempleDate());
		
		System.out.println("================================");
		
		context.close();

	}
}
