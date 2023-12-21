package com.formation.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.formation.domain.Duplicata;
import com.formation.formulaires.FormulaireConnexion;
import com.formation.formulaires.FormulaireDuplicata;
import com.formation.service.DuplicataService;

import jakarta.validation.Valid;

@Controller
public class WebControlleur {

	@Autowired
	private DuplicataService duplicataService;

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
	public String seconnecter(@ModelAttribute @Valid FormulaireConnexion formulaireSoumis, BindingResult bindingResult,
			Model model) {

		// faire le traitement en cas d'erreurs lors de la validation
		if (bindingResult.hasErrors()) {
			return "formulaireConnexion.html";
		}

		if (formulaireSoumis.getLogin().equals(formulaireSoumis.getPwd())) {
			return "redirect:/";
		}
		model.addAttribute("interdit", "true");
		return "formulaireConnexion.html";
	}

	@GetMapping("/listeDuplicatas")
	public String listeDuplicatas(Model model) {

		List<Duplicata> duplicatas = duplicataService.listDuplicatas();
		model.addAttribute("duplicatas", duplicatas);
		return "listeDuplicatas.html";

	}

	@GetMapping("/genererDuplicata")
	public String genererDuplicata(Model model) {
		model.addAttribute("formulaireDuplicata", new FormulaireDuplicata());
		return "genererDuplicata.html";
	}

	
	@PostMapping("/genererDuplicata")
	public String creerDuplicata(@ModelAttribute @Valid FormulaireDuplicata formulaireDuplicata, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "genererDuplicata.html";
		}

		Duplicata duplicataCree = duplicataService.createDuplicata(formulaireDuplicata.getUserId(),
				formulaireDuplicata.getMontant());

		model.addAttribute("duplicatas", duplicataService.listDuplicatas());
		return "redirect:/listeDuplicatas";
	}
	
	@GetMapping("/seloguer")
	public String seloguer() {
		return "customLogin.html";
	}
	
	@GetMapping("/sedeconnecter")
    public String logout() {
        return "redirect:/seloguer";
	}

}