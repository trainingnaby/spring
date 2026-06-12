package fr.formation.corscsrf.api.controleur;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.formation.corscsrf.api.modele.*;
import fr.formation.corscsrf.api.service.ServiceBanque;

@RestController
public class BanqueControleur {
	private final ServiceBanque serviceBanque;

	public BanqueControleur(ServiceBanque serviceBanque) {
		this.serviceBanque = serviceBanque;
	}

	@GetMapping("/api/compte")
	public Compte compte() {
		return serviceBanque.consulterCompte();
	}

	@PostMapping("/api/virement-json")
	public ResponseEntity<Map<String, Object>> virementJson(@RequestBody VirementJson demande) {
		String message = serviceBanque.faireVirement(demande.beneficiaire(), demande.montant());
		return ResponseEntity.ok(Map.of("message", message, "solde", serviceBanque.consulterCompte().getSolde()));
	}

	@PostMapping("/virement")
	public ResponseEntity<String> virementFormulaire(VirementFormulaire formulaire) {
		String message = serviceBanque.faireVirement(formulaire.getBeneficiaire(), formulaire.getMontant());
		return ResponseEntity.ok("<h1>" + message + "</h1><p><a href='/'>Retour banque</a></p>");
	}
}
