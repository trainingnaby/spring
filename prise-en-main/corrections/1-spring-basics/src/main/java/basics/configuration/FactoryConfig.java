package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import basics.beans.factoryconfig.Batiment;
import basics.beans.factoryconfig.Constructeur;
import basics.beans.factoryconfig.Eiffage;
import basics.beans.factoryconfig.MairieParis;
import basics.beans.factoryconfig.MaitreOuvrage;
import basics.beans.xmlconfig.Train;

@Configuration // Cette annotation indique que cette classe est une classe de configuration
				// Spring
@ImportResource(locations = { "classpath:/context.xml" }) // Cette annotation permet d'importer un fichier de
															// configuration XML dans la configuration Java
@Import(value = { AnnotationConfig.class })
public class FactoryConfig {

	@Bean(name = "trainBean") // Cette annotation indique que
	// cette méthode est un bean Spring et lui donne un nom "trainBean"
	public Train nom_methode_arbitraire() {
		return new Train();
	}

	@Bean("constructeur")
	public Constructeur constructeur() {
		return new Eiffage();
	}

	@Bean("maitreOuvrage")
	public MaitreOuvrage maitreOuvrage() {
		return new MairieParis();
	}

	@Bean("batiment")
	public Batiment batiment() {
		// créer un bean de type Batiment en utilisant le constructeur qui prend un
		// MaitreOuvrage en paramètre
		Batiment batiment = new Batiment(maitreOuvrage());
		batiment.setConstructeur(constructeur()); // injecter le constructeur dans le bean Batiment en utilisant le
													// setter
		return batiment;
	}

}
