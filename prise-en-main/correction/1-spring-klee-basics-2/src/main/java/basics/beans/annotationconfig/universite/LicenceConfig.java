package basics.beans.annotationconfig.universite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("licence")
public class LicenceConfig implements CursusConfig{

	@Bean("dataCursus")
	@Override
	public DonneesCours getDonneesCoursCursus() {
		DonneesCours donneesCours = new DonneesCours();
		donneesCours.setResponsable("Responsable Licence");
		donneesCours.setSalleCours("Salle Licence");
		donneesCours.setUrlCours("http://cours.licence.universite.com");
		return donneesCours;
	}

}
