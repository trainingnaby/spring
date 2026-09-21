package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import basics.beans.factoryconfig.Train;

@Configuration // dit à spring que cette classe contient des beans à gérer
@ImportResource(locations = {"classpath:/context.xml"}) // importe les beans dans le fichier xml
@Import(value = {AnnotationConfig.class}) // importe la config dans la classe AnnotationConfig
public class FactoryConfig {
	
	@Bean(name="trainBean")
	public Train nom_methode_arbitraire() {
		return new Train();
	}


	
}
