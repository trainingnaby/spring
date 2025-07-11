package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
//@Profile("dev")
public class DummyDuplicatas {
	
	@Autowired
	private DuplicataService duplicataService;
	
	@PostConstruct
	public void createDummyDuplicatas() {
		duplicataService.createDuplicata("AAA", 1000);
		duplicataService.createDuplicata("BBB", 4000);
		duplicataService.createDuplicata("CCC", 8900);
	}
	
}
