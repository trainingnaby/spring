package basics.beans.annotationconfig.universite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("licence")
public class LicenceConfig implements CursusConfig {

	@Bean("dataCursus")
	@Override
	public DonneesCours getDonneesCoursCursus() {
		DonneesCours dc = new DonneesCours();
		dc.setUrlCours("https://licence.universite.com");
		dc.setResponsable("Responsable Licence");
		dc.setSalleCours("Salle Licence");
		return dc;
	}

}