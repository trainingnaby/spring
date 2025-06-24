package com.formation.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.exception.DuplicataException;
import com.formation.projection.AppUserProjection;
import com.formation.service.DuplicataService;
import com.formation.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// @Controller =W
@RestController
@Validated
public class DuplicataControlleur {
	
	@Autowired
	private DuplicataService duplicataService;
	
	@GetMapping("/") 
	// @RequestMapping(method = RequestMethod.GET, path = "/duplicatas")
	public String home(){
		return "Hello !";
	}
	
	@GetMapping("/duplicatas") // GET http://localhost:8080/duplicatas
	// @RequestMapping(method = RequestMethod.GET, path = "/duplicatas")
	public List<Duplicata> listDuplicatas(){
		return duplicataService.listDuplicatas();
	}
	
	@GetMapping("/duplicata/{id}") // GET http://localhost:8080/duplicata/1
	public Duplicata getDuplicatas(@PathVariable long id) throws DuplicataException {
		return duplicataService.getDuplicata(id);
	}
	
	@PostMapping("/duplicata") // POST http://localhost:8080/duplicata?numeroFiscal=ZZZ&montant=1000&date=2005
	public Duplicata createDuplicata(@RequestParam String numeroFiscal, 
			@RequestParam @Min(0) @Max(5000) int montant, @RequestParam int date) {
		return duplicataService.createDuplicata(numeroFiscal, montant, date);
	}
	
	@PostMapping("/duplicata/{numeroFiscal}/{montant}/{annee}") // POST http://localhost:8080/duplicata/ZZZ/1000/2005
	public Duplicata createDuplicataPath(@PathVariable String numeroFiscal, 
			@PathVariable @Min(0) @Max(5000) int montant, @PathVariable int annee){
		return duplicataService.createDuplicata(numeroFiscal, montant, annee);
	}
	
	@PostMapping("/duplicata/dto")
	// POST http://localhost:8080/duplicata/dto {"numeroFiscal":"EEE", "montant":2000, "annee":2009}
	public Duplicata createDuplicataDto(@RequestBody @Valid DuplicataDto duplicataDto) {
		return duplicataService.createDuplicata(duplicataDto.getNumeroFiscal(), 
				duplicataDto.getMontant(), duplicataDto.getAnnee());
	}
	
	
	@GetMapping("/duplicatas/paging")
	public Map<String, Object> listDuplicatasPaging(@RequestParam int page, @RequestParam int size){
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Duplicata> resPageable = duplicataService.listDuplicatasPageable(pageable);
		
		Map<String, Object> results = new HashMap<>();
		List<Duplicata> duplicatas = resPageable.getContent();
		
		results.put("duplicatas", duplicatas);
		results.put("nbreDuplicatas", resPageable.getTotalElements());
		results.put("totalPages", resPageable.getTotalPages());
		results.put("pageActuelle", resPageable.getNumber());
		
		return results;
		
	}
	
	
	@GetMapping("/duplicatas/sorted")
	public List<Duplicata> listDuplicatasSorted(){
		return duplicataService.listDuplicatasSorted();
	}
	
	@GetMapping("/duplicata/existsgreater/{montant}")
	public boolean listDuplicatasSorted(@PathVariable int montant){
		return duplicataService.existsGreaterThan(montant);
	}
	
}
