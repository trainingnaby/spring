package basics.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import basics.beans.annotationconfig.Bateau;
import basics.beans.xmlconfig.Avion;
import basics.beans.xmlconfig.Train;
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
		context.close();
		
	}

}
