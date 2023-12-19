package com.formation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class FormationSpring implements FormationMetadata{
	
	@Value("${formation.module:Java}")
	private String module;
	
	@Value("${formation.salle:salleParDefaut}")
	private String salle;
 
	@Autowired
	private Environment environment;
	
	@Override
	public void lireMetadata() {
		System.out.println("Lieu formation :"
				+ environment.getProperty("formation.lieu"));
 
		System.out.println("Module formation : " + module);
		System.out.println("Salle formation : " +salle);
		
	}

}
