package com.formation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.formation.entity.Utilisateur;
import com.formation.repository.UserInfoRepository;

@Service
public class UserInfoService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<Utilisateur> userDB = repository.findByName(username);

		// Convertir en UserDetails
		return userDB.map(UserInfoDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable" + username));
	}

	public String addUser(Utilisateur userInfo) {
		// hasher le mot de passe avant de le stocker
		userInfo.setPassword(encoder.encode(userInfo.getPassword()));
		repository.save(userInfo);
		return "Utilisateur ajouté !";
	}


}
