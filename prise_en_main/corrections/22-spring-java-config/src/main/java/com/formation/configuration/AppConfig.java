package com.formation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import com.formation.domain.Avion;

@Configuration // cette classe sert à configurer Spring IoC
public class AppConfig {

	// factory (usine) méthode qui permet de déclarer les beans qui seront gérés par Spring
	@Bean(name="avionBean")
	public Avion nom_methode_arbitraire() {
		return new Avion();
	}

}