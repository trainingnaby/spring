package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.AppUser;
import com.formation.dto.UserDto;
import com.formation.projection.AppUserProjection;
import com.formation.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserControlleur {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/user/dto")
	public AppUser createDuplicataDto(@RequestBody @Valid UserDto userDto) {
		return userService.createUser(userDto.getNumeroFiscal(), 
				userDto.getUsername(), userDto.getPassword());
	}
	
	

	@GetMapping("/users/projection")
	public List<AppUserProjection> listUsersProjected(){
		return userService.listUserProjection();
	}
	

}
