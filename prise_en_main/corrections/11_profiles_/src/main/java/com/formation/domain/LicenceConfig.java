package com.formation.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration // permet de créer les beans qui seront gérés par Spring
@Profile("licence") // classe chargée uniquement si le profile licence est activé au démarrage
public class LicenceConfig implements CursusConfig{

	@Bean("data")
	@Override
	public Donnees getDonneesCursus() {
		
		Donnees donneesLicence = new Donnees();
		donneesLicence.setResponsable("Responsable Licence");
		donneesLicence.setSalleCours("Salle Licence");
		donneesLicence.setUrlCours("https://licence.univ");
		return donneesLicence;
	}

}
