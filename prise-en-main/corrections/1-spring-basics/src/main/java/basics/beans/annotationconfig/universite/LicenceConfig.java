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
		dc.setResponsable("Dr. Dupont Licence");
		dc.setSalleCours("Salle licence 101");
		dc.setUrlCours("http://univ.example.com/licence");
		return dc;
	}

}
