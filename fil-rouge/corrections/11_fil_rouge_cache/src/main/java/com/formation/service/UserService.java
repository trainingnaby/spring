package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

import jakarta.annotation.PostConstruct;

//classe du domaine => ne necessite pas d'annotation @Component, @Service, @Repository, etc.
//c'est une classe simple qui représente une entité métier, elle n'est pas gérée par Spring
@Service
public class UserService {
	
	public User findById(String id) {
		
		String userIdInDB = UUID.randomUUID().toString();
		User user = new User();
		user.setId(userIdInDB);
		user.setName("User " + userIdInDB);
		return user;
	}
	
	@PostConstruct // cette méthode sera appelée automatiquement par Spring après l'instanciation du bean et l'injection de ses dépendances
	public void init() {
		System.out.println("UserService initialized with dummy data");
	}

}
