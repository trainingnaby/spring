package com.formation.web;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {
	@GetMapping("/")
	public String accueil() {
		return "accueil";
	}

	@GetMapping("/espace-client")
	public String client(Principal principal, Model model) {
		model.addAttribute("utilisateur", principal.getName());
		return "client";
	}

	@GetMapping("/admin")
	public String admin(Principal principal, Model model) {
		model.addAttribute("utilisateur", principal.getName());
		return "admin";
	}
}
