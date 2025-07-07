package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import basics.beans.factoryconfig.Train;

@Configuration
@ImportResource(locations = {"classpath:/context.xml"})
@Import(value = {AnnotationConfig.class})
public class FactoryConfig {

	@Bean(name = "trainBean")
	public Train nom_methode_arbitraire() {
		return new Train();
	}

}