package com.formation.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.formation.domain.User;

@Service
public class UserService {

	public User findById(String userId) {

		String userInDB = UUID.randomUUID().toString();

		User user = new User();
		user.setId(userInDB);
		user.setName("User " + userInDB);
		return user;
	}

}
