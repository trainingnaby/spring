package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;

public class DummyDuplicatas {

	
	@Autowired
	private DuplicataService duplicataService;
	
	@PostConstruct
	public void initDuplicatas() {
		
		duplicataService.createDuplicata("AAA", 3000, 2005);
		duplicataService.createDuplicata("BBB", 4000, 1999);
		duplicataService.createDuplicata("CCC", 7000, 2010);
	}
	
}
