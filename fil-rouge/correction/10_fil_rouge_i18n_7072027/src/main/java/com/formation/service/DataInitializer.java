package com.formation.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.formation.domain.UserApp;
import com.formation.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner{
	
	@Autowired
	private UserRepository appUserRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		
		UserApp user = new UserApp();
		user.setId(UUID.randomUUID().toString());
		user.setUsername("user");
		user.setPassword(passwordEncoder.encode("user"));
		user.setRole("USER");
		appUserRepository.save(user);
		
		UserApp user2 = new UserApp();
		user2.setId(UUID.randomUUID().toString());
		user2.setUsername("admin");
		user2.setPassword(passwordEncoder.encode("admin"));
		user2.setRole("ADMIN");
		
		appUserRepository.save(user2);
		
	}
	
	
	

}
