package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import basics.beans.factoryconfig.Batiment;
import basics.beans.factoryconfig.Constructeur;
import basics.beans.factoryconfig.Eiffage;
import basics.beans.factoryconfig.MairieParis;
import basics.beans.factoryconfig.MaitreOuvrage;
import basics.beans.factoryconfig.Train;

@Configuration
@ImportResource(locations = {"classpath:/context.xml"}) // scanner aussi les beans de la config XML
@Import(value = {AnnotationConfig.class}) // scanner les beans de la config sur AnnotationConfig
@PropertySource(value = { "classpath:application.properties" })
public class FactoryConfig {

	@Bean(name="trainBean")
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
		Batiment batiment = new Batiment(maitreOuvrage());
		batiment.setConstructeur(constructeur());
		return batiment;
		
	}

}
