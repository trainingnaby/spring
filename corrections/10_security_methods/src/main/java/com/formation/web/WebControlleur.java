package com.formation.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.formation.domain.AppUser;
import com.formation.domain.Duplicata;
import com.formation.exception.DuplicataException;
import com.formation.exception.UserException;
import com.formation.formulaires.FormulaireDuplicata;
import com.formation.formulaires.FormulaireUser;
import com.formation.service.DuplicataService;
import com.formation.service.UserService;

import jakarta.validation.Valid;

@Controller
public class WebControlleur {

	@Autowired
	private DuplicataService duplicataService;
	
	@Autowired
	UserService userService;
	

	@GetMapping("/")
	public String homepage(Model model) {
		model.addAttribute("dateActuelle", LocalDateTime.now());
		return "index.html";
	}

	@GetMapping("/listeDuplicatas")
	public String listeDuplicatas(Model model) {

		List<Duplicata> duplicatas = duplicataService.listDuplicatas();
		model.addAttribute("duplicatas", duplicatas);
		return "listeDuplicatas.html";

	}

	@PreAuthorize("hasRole('READ')")
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
		
		try {
			Duplicata duplicataCree = duplicataService.createDuplicata(formulaireDuplicata.getNumeroFiscal(),
					formulaireDuplicata.getMontant(), formulaireDuplicata.getAnnee());

			model.addAttribute("duplicatas", duplicataService.listDuplicatas());
			return "redirect:/listeDuplicatas";

		} catch(UserException e) {
			model.addAttribute("numeroFiscalNotExists", true);
			return "genererDuplicata.html";
		}

		
	}
	
	@GetMapping("/afficherDuplicata/{id}")
	public String afficherDuplicata(@PathVariable long id, Model model) throws DuplicataException {
			Duplicata duplicata = duplicataService.getDuplicata(id);
			model.addAttribute("duplicata", duplicata);
			return "afficherDuplicata.html";
	}

	@GetMapping("/supprimerDuplicata/{id}")
	public String supprimerDuplicata(@PathVariable long id, Model model) throws DuplicataException {
		
		Duplicata duplicataAsupprimer = duplicataService.getDuplicata(id);
		if(duplicataAsupprimer != null) {
			duplicataService.supprimerDuplicata(duplicataAsupprimer);
		}
		model.addAttribute("duplicatas", duplicataService.listDuplicatas());
		return "redirect:/listeDuplicatas";
	}
	
	@GetMapping("/creerUser")
	public String creerCompte(Model model) {
		model.addAttribute("formulaireUser", new FormulaireUser());
		return "creerUser.html";
	}

	
	@PostMapping("/creerUser")
	public String creerDuplicata(@ModelAttribute @Valid FormulaireUser formulaireUser, BindingResult bindingResult,
			Model model) throws UserException {

		if (bindingResult.hasErrors()) {
			return "creerUser.html";
		}

		try {
			AppUser user = userService.createUser(formulaireUser.getNumeroFiscal(), formulaireUser.getUsername(), 
					formulaireUser.getPassword());
			model.addAttribute("user", user);
			return "customLogin.html";

		} catch(UserException e) {
			model.addAttribute("userAlreadyExists", true);
			return "creerUser.html";
		}
		
		
	}
	

}