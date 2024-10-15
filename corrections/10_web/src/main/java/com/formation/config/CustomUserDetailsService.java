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
import org.springframework.stereotype.Service;

import com.formation.domain.AppUser;
import com.formation.repository.AppUserRepository;


// cette classe va permettre de rechercher et croiser les infos du user en base de données et 
// de croiser ces infos avec les infos du login
@Component
public class CustomUserDetailsService implements UserDetailsService{

	
	@Autowired
	AppUserRepository appUserRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		AppUser appUser = appUserRepository.findByUsername(username);
		
		//récupération des roles : ADMIN,USER
		List<String> roles = Arrays.asList(appUser.getAuthorities());
		List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
				.map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());
		
		// retourne de type UserDetails
		return new User(appUser.getUsername(), appUser.getPassword(), grantedAuthorities);
	}

}
