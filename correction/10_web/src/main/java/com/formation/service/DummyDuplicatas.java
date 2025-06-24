package com.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formation.exception.UserException;

import jakarta.annotation.PostConstruct;

@Service
public class DummyDuplicatas {
	
	@Autowired 
	private DuplicataService duplicataService;
	
	@Autowired
	private UserService userService;
	
	
	@PostConstruct
	public void initDuplicatas() throws UserException {
		

		userService.createUser("AAA", "AAA", "AAA");
		userService.createUser("BBB", "BBB", "BBB");
		userService.createUser("CCC", "CCC", "CCC");
		
		duplicataService.createDuplicata("AAA", 1000, 2005);
		duplicataService.createDuplicata("BBB", 4000, 2014);
		duplicataService.createDuplicata("CCC", 8900, 2023);
		
	}

}
