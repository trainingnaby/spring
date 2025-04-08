package com.formation.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.service.DuplicataService;

@RestController
public class DuplicataControlleur {

	// si des actions métiers sont à faire, appeler les beans de la couche métier
	@Autowired
	private final DuplicataService duplicataService;

	public DuplicataControlleur(DuplicataService duplicataService) {
		this.duplicataService = duplicataService;
	}

	@GetMapping("/duplicatas")
	// @RequestMapping(value = "/duplicatas", method = RequestMethod.GET)
	public List<Duplicata> duplicatas() {
		return duplicataService.getDuplicatas();
	}
	
	@PostMapping("/duplicatas")
	public Duplicata createDuplicata(@RequestParam("user_id") String userId, @RequestParam Integer montant) {
		return duplicataService.createDuplicata(userId, montant);
	}
	
	 @PostMapping("/duplicatas/{userId}/{montant}")
	  public Duplicata createDuplicata_path(@PathVariable String userId, @PathVariable Integer montant) {
		 return duplicataService.createDuplicata(userId, montant);
	 }
	 
	 @PostMapping("/duplicatas_dto")
     public Duplicata createDuplicata_dto(@RequestBody DuplicataDto duplicataDto) {
	 	return duplicataService.createDuplicata(duplicataDto.getUserId(), duplicataDto.getMontant());
     }
		
	
	
}