package com.formation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formation.domain.Addresse;
import com.formation.domain.AppUser;
import com.formation.exception.UserException;
import com.formation.projection.AppUserProjection;
import com.formation.projection.UserProjectionDto;
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
	
	public AppUser createUser(String numeroFiscal, String username, String password) throws UserException {
		
		if(userRepository.existsByNumeroFiscal(numeroFiscal)) {
			throw new UserException("Un utilisateur existe déjà avec le numero fiscal "+numeroFiscal);
		}
		
		AppUser newUser = new AppUser();
		
		newUser.setNumeroFiscal(numeroFiscal);
		newUser.setPassword(password);
		newUser.setEnabled(1);
		newUser.setUsername(username);
		newUser.setAuthorities("ROLE_USER");
		
		Addresse addresse = new Addresse();
		addresse.setCodePostal(7500);
		addresse.setNumero(140);
		addresse.setRue("Rue de Rivoli");
		addresse.setVille("Paris");
		
		newUser.setAddresse(addresse); 
		
		AppUser savedUser = userRepository.save(newUser);
		
		return savedUser;
	}
	
	public List<UserProjectionDto> listUserProjection(){
		return userRepository.findAllProjectedBy();
	}
	

	public AppUserProjection findByNumeroFiscalDynamicProjection(String numeroFiscal) throws UserException{
		return userRepository.findByNumeroFiscal(numeroFiscal, AppUserProjection.class);
	}

}
