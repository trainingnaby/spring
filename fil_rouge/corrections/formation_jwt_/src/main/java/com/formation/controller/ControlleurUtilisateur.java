package com.formation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formation.entity.RequeteLogin;
import com.formation.entity.Utilisateur;
import com.formation.service.JwtService;
import com.formation.service.UserInfoService;

@RestController
@RequestMapping("/authentification")
public class ControlleurUtilisateur {

	@Autowired
	private UserInfoService service;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/accueil")
	public String accueil() {
		return "Bienvenue à la page publique !";
	}

	@PostMapping("/ajouter_user")
	public String ajouterUser(@RequestBody Utilisateur userInfo) {
		return service.addUser(userInfo);
	}

	@GetMapping("/user/espace_user")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String espace_user() {
		return "Bienvenue à votre page personelle !";
	}

	@GetMapping("/admin/espace_admin")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String espace_admin() {
		return "Bienvenue à la page admin !";
	}

	@PostMapping("/generer_token")
	public String authentificationEtGenerationToken(@RequestBody RequeteLogin authRequest) {
		// appel à authenticationManager pour s'authentifier
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		//génération d'un token
		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(authRequest.getUsername());
		} else {
			throw new UsernameNotFoundException(" requete invalide !");
		}
	}

}
