package com.formation.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.formation.domain.UserApp;
import com.formation.repository.UserRepository;

@Component
public class LoadUserFromDatabase implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// récupération de l'utilisateur depuis la base de données
		UserApp user = userRepository.findByUsername(username);
		
		// récuperation des roles de l'utilisateur	
		List<String> roles = Arrays.asList(user.getRole());
		List<SimpleGrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
		
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
		
	}
	
	

}
