package com.formation.web;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.formation.service.DuplicataService;

import jakarta.validation.Valid;


@Controller
public class WebControlleur {
	
	@Autowired 
	DuplicataService duplicataService;
	
	@GetMapping("/listeDuplicatas")
	public String listeDuplicatas(Model model) {
		model.addAttribute("duplicatas", duplicataService.getDuplicatas());
		return "listeDuplicatas.html";
	}
	
	@GetMapping("/genererDuplicata")
	public String pageCreationDuplicata(Model model) {
		model.addAttribute("formulaireDuplicata", new FormulaireDuplicata());
		return  "genererDuplicata.html";
	}
	
	@PostMapping("/genererDuplicata")
	public String traitementFormulaireDuplicata(@ModelAttribute @Valid FormulaireDuplicata formulaireDuplicata,
			BindingResult bindingResult, Model model) {
		
		// verifier si on a des erreurs de validation
		if(bindingResult.hasErrors()) {
			return "genererDuplicata.html";
		}
		
		duplicataService.createDuplicata(formulaireDuplicata.getUserId(), formulaireDuplicata.getMontant());
		// instruction de redirection sur http://localhost:8080/listeDuplicatas donnée au navigateur
		return "redirect:/listeDuplicatas";
	}
	
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