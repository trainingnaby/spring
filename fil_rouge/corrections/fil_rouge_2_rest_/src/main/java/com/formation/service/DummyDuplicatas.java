package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Profile("dev")
public class DummyDuplicatas {
	
	@Autowired
	DuplicataService duplicataService;
	
	@PostConstruct
	public void create_sample_duplicatas() {
		duplicataService.createDuplicata("AAAAA", 6000);
		duplicataService.createDuplicata("BBBBB", 2500);
		duplicataService.createDuplicata("CCCCC", 3900);
	}

}
