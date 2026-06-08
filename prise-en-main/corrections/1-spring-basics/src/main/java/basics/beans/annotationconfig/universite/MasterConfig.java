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
		DonneesCours dc = new DonneesCours();
		dc.setResponsable("Dr. Dupont Master");
		dc.setSalleCours("Salle master 101");
		dc.setUrlCours("http://univ.example.com/master");
		return dc;
	}

}
