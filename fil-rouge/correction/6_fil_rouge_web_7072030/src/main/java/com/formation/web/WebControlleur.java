package com.formation.web;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebControlleur {

	@GetMapping("/")
	public String homepage(Model model, @RequestParam(required = false, defaultValue = "Individu") String prenom) {
		model.addAttribute("prenom", prenom);
		model.addAttribute("dateActuelle", LocalDateTime.now());
		return "index.html";
	}

	@GetMapping("/seconnecter")
	public String seconnecter(Model model) {
		model.addAttribute("formulaireConnexion", new FormulaireConnexion());
		return "formulaireConnexion.html";
	}

	@PostMapping("/seconnecter")
	public String seconnecter(@ModelAttribute FormulaireConnexion formulaireSoumis, Model model) {
		if (formulaireSoumis.getLogin().equals(formulaireSoumis.getPwd())) {
			return "redirect:/";
		}
		model.addAttribute("interdit", "true");
		return "formulaireConnexion.html";
	}
}