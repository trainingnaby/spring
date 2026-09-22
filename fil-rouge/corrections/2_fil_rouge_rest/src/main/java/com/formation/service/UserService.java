package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

// Classe de service pour gérer les utilisateurs
// Spring doit le gérer comme un bean de type service, donc on utilise l'annotation @Service
@Service
public class UserService {
	
	public User findById(String id) {
		
		String userInDB = UUID.randomUUID().toString();
		User user = new User();
		user.setId(userInDB);
		user.setNom("User"+userInDB);
		return user;
		
	}

}
