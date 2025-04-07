package basics.beans.annotationconfig.universite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("master") // tous les beans qui seront générés via cette classe seront chargés lorsque on
// démarre l'application avec le profile "master"
@Configuration
public class MasterConfig implements CursusConfig{

	@Bean("dataCursus")
	@Override
	public DonneesCours getDonneesCoursCursus() {
		DonneesCours donneesCours = new DonneesCours();
		donneesCours.setResponsable("responsable master");
		donneesCours.setSalleCours("salle master");
		donneesCours.setUrlCours("https://master.com");
		return donneesCours;
	}

}
