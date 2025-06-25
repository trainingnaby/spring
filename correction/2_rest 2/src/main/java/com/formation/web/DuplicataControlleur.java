package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.exception.DuplicataException;
import com.formation.service.DuplicataService;

// @Controller =W
@RestController
public class DuplicataControlleur {
	
	@Autowired
	private DuplicataService duplicataService;
	
	
	@GetMapping("/duplicatas") // GET http://localhost:8080/duplicatas
	// @RequestMapping(method = RequestMethod.GET, path = "/duplicatas")
	public List<Duplicata> listDuplicatas(){
		return duplicataService.listDuplicatas();
	}
	
	@GetMapping("/duplicata/{id}") // GET http://localhost:8080/duplicata/1
	public Duplicata getDuplicatas(@PathVariable String id) throws DuplicataException {
		return duplicataService.getDuplicata(id);
	}
	
	@PostMapping("/duplicata") // POST http://localhost:8080/duplicata?numeroFiscal=ZZZ&montant=1000&date=2005
	public Duplicata createDuplicata(@RequestParam String numeroFiscal, 
			@RequestParam int montant, @RequestParam int date) {
		return duplicataService.createDuplicata(numeroFiscal, montant, date);
	}
	
	@PostMapping("/duplicata/{numeroFiscal}/{montant}/{annee}") // POST http://localhost:8080/duplicata/ZZZ/1000/2005
	public Duplicata createDuplicataPath(@PathVariable String numeroFiscal, 
			@PathVariable int montant, @PathVariable int annee){
		return duplicataService.createDuplicata(numeroFiscal, montant, annee);
	}
	
	@PostMapping("/duplicata/dto")
	// POST http://localhost:8080/duplicata/dto {"numeroFiscal":"EEE", "montant":2000, "annee":2009}
	public Duplicata createDuplicataDto(@RequestBody DuplicataDto duplicataDto) {
		return duplicataService.createDuplicata(duplicataDto.getNumeroFiscal(), 
				duplicataDto.getMontant(), duplicataDto.getAnnee());
	}

}
