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
	public void initDuplicatas() {
		duplicataService.createDuplicata("AAA", 2000, 2005);
		duplicataService.createDuplicata("BBB", 1500, 1999);
		duplicataService.createDuplicata("CCC", 3800, 1017);
	}
	
	

}
