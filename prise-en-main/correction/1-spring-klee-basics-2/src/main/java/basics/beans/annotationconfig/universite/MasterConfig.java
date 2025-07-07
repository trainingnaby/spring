package basics.beans.annotationconfig.universite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("master")
public class MasterConfig implements CursusConfig {

	@Bean("dataCursus")
	@Override
	public DonneesCours getDonneesCoursCursus() {
		DonneesCours donneesCours = new DonneesCours();
		donneesCours.setResponsable("Responsable Master");
		donneesCours.setSalleCours("Salle master");
		donneesCours.setUrlCours("http://cours.master.universite.com");
		return donneesCours;
	}

}
