package fr.formation.corscsrf.api.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import fr.formation.corscsrf.api.service.ServiceBanque;

@Controller
public class PageBanqueControleur {
	private final ServiceBanque serviceBanque;

	public PageBanqueControleur(ServiceBanque serviceBanque) {
		this.serviceBanque = serviceBanque;
	}

	@GetMapping("/")
	public String accueil(Model model) {
		model.addAttribute("compte", serviceBanque.consulterCompte());
		return "accueil";
	}

	@GetMapping("/virement")
	public String pageVirement(Model model) {
		model.addAttribute("compte", serviceBanque.consulterCompte());
		return "virement";
	}
}
