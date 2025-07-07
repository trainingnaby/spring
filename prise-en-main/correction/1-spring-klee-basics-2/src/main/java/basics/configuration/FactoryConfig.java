package basics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import basics.beans.factoryconfig.Train;

@Configuration // Indique que cette classe est une configuration Spring : peut générer des beans pour
// les méthodes annotées avec @Bean
public class FactoryConfig {

	@Bean(name = "trainBean")
	public Train nom_methode_arbitraire() {
		return new Train();
	}

}