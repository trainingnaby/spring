package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

@Service
public class UserService {
	
	public User findById(String userId) {
		// générer un user id aleatoire
		String user_id_in_DB = UUID.randomUUID().toString();
		
		User user = new User();
		user.setId(user_id_in_DB);
		user.setName(user_id_in_DB);
		return user;
	}

}
