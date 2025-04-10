package com.formation.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.formation.domain.Users;
import com.formation.repository.UsersRepository;

@Service
public class CustomDatabaseUserDetailsService implements UserDetailsService {

	@Autowired
	UsersRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// récuperer les infos du user en base de données (avec uniquement le username)
		Users user = userRepository.findByUsername(username);
		
		// récuppération des roles
		List<String> roles = Arrays.asList(user.getAuthorities());
		List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
				.map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());
		
		// retourner un objet de type User
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				grantedAuthorities);
	}
}
