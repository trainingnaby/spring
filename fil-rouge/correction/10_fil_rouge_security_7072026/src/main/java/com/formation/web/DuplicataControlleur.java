package com.formation.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.service.DuplicataService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@Validated
public class DuplicataControlleur {

	@Autowired
	private final DuplicataService duplicataService;

	public DuplicataControlleur(DuplicataService duplicataService) {
		this.duplicataService = duplicataService;
	}

	@GetMapping("/duplicatas")
	// @RequestMapping(value = "/duplicatas", method = RequestMethod.GET)
	public List<Duplicata> duplicatas() {
		return duplicataService.listDuplicatas();
	}

	@PostMapping("/duplicatas")
	// http://localhost:8080/duplicatas?montant=100&user_id=naby
	public Duplicata createDuplicata(@RequestParam("user_id") String userId,
			@RequestParam @Min(1000) @Max(7000) int montant) {
		return duplicataService.createDuplicata(userId, montant);
	}

	@PostMapping("/duplicatas/{userId}/{montant}")
	public Duplicata createDuplicatapath(@PathVariable String userId,
			@PathVariable @Min(1000) @Max(7000) Integer montant) {
		return duplicataService.createDuplicata(userId, montant);
	}

	@PostMapping("/duplicatas_dto")
	public Duplicata createDuplicata_dto(@RequestBody @Valid DuplicataDto duplicataDto) {
		return duplicataService.createDuplicata(duplicataDto.getUserId(), duplicataDto.getMontant());
	}

	@GetMapping("/paging")
	public ResponseEntity<Map<String, Object>> getDuplicatasPaging(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {

		List<Duplicata> duplicatas = new ArrayList<Duplicata>();
		Pageable paging = PageRequest.of(page, size);

		Page<Duplicata> pageDuplicatas;
		pageDuplicatas = duplicataService.findAllByPaging(paging);

		duplicatas = pageDuplicatas.getContent();

		Map<String, Object> response = new HashMap<>();
		response.put("duplicatas", duplicatas);
		response.put("totalDuplicatas", pageDuplicatas.getTotalElements());
		response.put("totalPages", pageDuplicatas.getTotalPages());
		response.put("pageActuelle", pageDuplicatas.getNumber());

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}