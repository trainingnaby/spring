package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Profile("dev")
public class DummyDuplicatas {
	
	@Autowired
	private DuplicataService duplicataService;
	
	@PostConstruct
	public void init() {
		duplicataService.createDuplicata("AAA", 2000);
		duplicataService.createDuplicata("BBB", 3000);
		duplicataService.createDuplicata("CCC", 4000);
		
		System.out.println("Dummy duplicatas created for development profile.");
	}
	

}
