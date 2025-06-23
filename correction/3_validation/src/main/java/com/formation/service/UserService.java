package com.formation.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.formation.domain.AppUser;
import com.formation.exception.UserException;

@Service
public class UserService {

	List<AppUser> users = new CopyOnWriteArrayList<>();

	public AppUser findByNumeroFiscal(String numeroFiscal) throws UserException {
		for (AppUser currentUser : users) {
			if (currentUser.getNumeroFiscal().equals(numeroFiscal)) {
				return currentUser;
			}
		}
		throw new UserException("User not found");
	}

	public AppUser createUser(String numeroFiscal, String username, String password) {
		AppUser newUser = new AppUser();
		newUser.setId(UUID.randomUUID().toString());
		newUser.setNumeroFiscal(numeroFiscal);
		newUser.setEnabled(1);
		newUser.setUsername(username);
		
		users.add(newUser);
		return newUser;
	}

}
