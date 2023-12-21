package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

@Service
public class UserService {
	
	public User findById(String userId) {
		
		// simulation recherche user en base
		String user_name_db = UUID.randomUUID().toString();
		
		User user = new User();
		user.setId(userId);
		user.setName(user_name_db);
		
		return user;
		
	}

}
