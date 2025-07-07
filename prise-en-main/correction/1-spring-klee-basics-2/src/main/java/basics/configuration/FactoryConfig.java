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
import basics.beans.factoryconfig.Train;

@Configuration
@ImportResource(locations = {"classpath:/context.xml"})
@Import(value = {AnnotationConfig.class})
public class FactoryConfig {

	@Bean(name = "trainBean")
	public Train nom_methode_arbitraire() {
		return new Train();
	}
	
	@Bean("constructeur")
	public Constructeur constructeur() {
		return new Eiffage();
	}
	
	@Bean(name = "maitreOuvrage")
	public MaitreOuvrage maitreOuvrage() {
		return new MairieParis();
	}
	
	@Bean(name = "batiment")
	public Batiment batiment() {
		Batiment batiment = new Batiment(maitreOuvrage());
		batiment.setConstructeur(constructeur());
		return batiment;
	}

}