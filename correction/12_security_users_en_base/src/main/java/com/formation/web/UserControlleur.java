package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.AppUser;
import com.formation.dto.UserDto;
import com.formation.exception.UserException;
import com.formation.projection.UserProjectionDto;
import com.formation.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserControlleur {
	
	@Autowired
	UserService userService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@PostMapping("/user/dto")
	public AppUser createDuplicataDto(@RequestBody @Valid UserDto userDto) throws UserException {
		return userService.createUser(userDto.getNumeroFiscal(), 
				userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()));
	}
	
	

	@GetMapping("/users/projection")
	public List<UserProjectionDto> listUsersProjected(){
		return userService.listUserProjection();
	}
	

}
