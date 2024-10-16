package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.AppUser;
import com.formation.dto.LoginDto;
import com.formation.dto.UserDto;
import com.formation.exception.UserException;
import com.formation.projection.UserProjectionDto;
import com.formation.service.JWTService;
import com.formation.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserControlleur {
	
	@Autowired
	UserService userService;
	
	// bean permettantt de s'authentifier
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTService jwtService;
	
	@PostMapping("/user/create")
	public AppUser createDuplicataDto(@RequestBody @Valid UserDto userDto) throws UserException {
		return userService.createUser(userDto.getNumeroFiscal(), 
				userDto.getUsername(), userDto.getPassword());
	}
	
	@PostMapping("/user/token")
	public String authAndTokenGeneration(@RequestBody LoginDto loginDto) {
		
		// tentative d'authentification
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), 
				loginDto.getPassword()));
		
		// génération du token
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(loginDto.getUsername());
		} else {
			throw new UsernameNotFoundException("requête invalide");
		}
	
	}
	

	@GetMapping("/users/projection")
	public List<UserProjectionDto> listUsersProjected(){
		return userService.listUserProjection();
	}
	

}
