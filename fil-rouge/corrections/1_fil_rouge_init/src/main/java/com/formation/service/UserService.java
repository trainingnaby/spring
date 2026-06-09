package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

@Service
public class UserService {
	
	public User findById(String id) {
		
		String userIdInDB = UUID.randomUUID().toString();
		User user = new User();
		user.setId(userIdInDB);
		user.setName("User " + userIdInDB);
		return user;
	}

}
