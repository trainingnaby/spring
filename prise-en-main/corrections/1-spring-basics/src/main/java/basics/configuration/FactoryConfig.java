package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import basics.beans.xmlconfig.Train;

@Configuration // Cette annotation indique que cette classe est une classe de configuration Spring
@ImportResource(locations = {"classpath:/context.xml"}) // Cette annotation permet d'importer un fichier de configuration XML dans la configuration Java
@Import(value = {AnnotationConfig.class})
public class FactoryConfig {

	@Bean(name = "trainBean") // Cette annotation indique que 
	// cette méthode est un bean Spring et lui donne un nom "trainBean"
	public Train nom_methode_arbitraire() {
		return new Train();
	}

}
