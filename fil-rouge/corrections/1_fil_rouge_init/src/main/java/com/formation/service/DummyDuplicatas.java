package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Profile("dev")
public class DummyDuplicatas {
	
	@Autowired
	private DuplicataService duplicatasService;
	
	@PostConstruct
	public void createDummyDuplicatas() {
		duplicatasService.createDuplicata("Amelle", 1900);
		duplicatasService.createDuplicata("Richard", 2000);
		duplicatasService.createDuplicata("André", 3500);
	}

}
