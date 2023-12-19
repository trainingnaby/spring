package com.formation.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("master")
public class MasterConfig implements CursusConfig{

	@Bean("data")
	@Override
	public Donnees getDonneesCursus() {
		
		Donnees donneesMaster = new Donnees();
		donneesMaster.setResponsable("Responsable Master");
		donneesMaster.setSalleCours("Salle Master");
		donneesMaster.setUrlCours("https://master.univ");
		return donneesMaster;
	}

}
