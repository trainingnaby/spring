package com.formation.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.formation.domain.AppUser;
import com.formation.repository.AppUserRepository;

@Component
public class DuplicataUserDetailsService implements UserDetailsService{
	
	@Autowired
	AppUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		AppUser appUser = userRepository.findByUsername(username);
		
		// Les roles sont stockées de cette manière en base : ADMIN, USER, SUPER
		// Récupére les roles et les recrée sous forme de liste 
		List<String> roles = Arrays.asList(appUser.getAuthorities());
		List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
				.map(authority -> new SimpleGrantedAuthority(authority))
				.collect(Collectors.toList());
		
		
	return new User(appUser.getUsername(),
			appUser.getPassword(),
			grantedAuthorities);
	}

}
