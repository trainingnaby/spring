package com.formation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formation.domain.AppUser;
import com.formation.exception.UserException;
import com.formation.repository.AppUserRepository;

@Service
public class UserService {
	
	@Autowired
	AppUserRepository userRepository;
	

	public AppUser findByNumeroFiscal(String numeroFiscal) throws UserException{
		return userRepository.findByNumeroFiscal(numeroFiscal).orElseThrow(() -> 
			 new UserException("User not found ! ")
		);
	}
	
	public List<AppUser> findAllUsers(){
		return userRepository.findAll(); 
	}
	
	public AppUser createUser(String numeroFiscal, String username, String password) {
		AppUser newUser = new AppUser();
		
		String randomId = UUID.randomUUID().toString();
		
		newUser.setId(randomId);
		newUser.setNumeroFiscal(numeroFiscal);
		newUser.setPassword(password);
		newUser.setEnabled(1);
		newUser.setUsername(username);
		
		AppUser savedUser = userRepository.save(newUser);
		
		return savedUser;
	}

}
