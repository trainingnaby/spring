package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.service.DuplicataService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController // permet de définir une classe comme un controlleuer
				// renvoie directement le contenu des réponses
@RequestMapping("/api") // traitera toutes les requêtes qui
						// sur /api 
@Validated  // nécessaire pour valider les paramètres de requetes
public class DuplicataControlleur {
	
	@Autowired
	private DuplicataService duplicataService;
	
	@GetMapping("/duplicatas") // traite toutes les requêtes en GET qui arrivent sur /api/duplicatas
	public List<Duplicata> listDuplicatas(){
		return duplicataService.listDuplicatas();
	}
	
	@GetMapping("/duplicata/{id}")  // traite toutes les requetes GET sur /duplicata/{id}
	public Duplicata getDuplicata(@PathVariable String id) throws Exception {
		return duplicataService.getDuplicata(id);
	}
	
	@GetMapping("/duplicata")
	public Duplicata createDuplicata(@RequestParam String numeroFiscal, 
			@RequestParam @Min(0) @Max(10000) int montant, @RequestParam int date) {
		return duplicataService.createDuplicata(numeroFiscal, montant, date);
	}
	
	@GetMapping("/duplicata/{numeroFiscal}/{montant}/{annee}")
	public Duplicata createDuplicataPath(@PathVariable String numeroFiscal, 
			@PathVariable @Min(0) @Max(10000) int montant, @PathVariable int annee) {
		return duplicataService.createDuplicata(numeroFiscal, montant, annee);
	}
	
	@PostMapping("duplicata/dto")
	public Duplicata createDuplicataDto(@RequestBody @Valid DuplicataDto duplicataDto) {
		return duplicataService.createDuplicata(duplicataDto.getNumeroFiscal(),
				duplicataDto.getMontant(), duplicataDto.getAnnee());
	}
	
	
	
	

}
