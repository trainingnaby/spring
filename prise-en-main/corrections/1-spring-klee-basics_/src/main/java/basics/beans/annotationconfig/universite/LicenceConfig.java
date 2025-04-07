package basics.beans.annotationconfig.universite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
//@Profile("licence") // tous les beans qui seront générés via cette classe seront chargés lorsque on
					// démarre l'application avec le profile "licence"
public class LicenceConfig implements CursusConfig{

	@Bean("dataCursus")
	@Override
	public DonneesCours getDonneesCoursCursus() {
		DonneesCours donneesCours = new DonneesCours();
		donneesCours.setResponsable("responsable licence");
		donneesCours.setSalleCours("salle licence");
		donneesCours.setUrlCours("https://licence.com");
		return donneesCours;
	}

}
