package com.formation.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.domain.RequeteLogin;
import com.formation.dto.DuplicataDto;
import com.formation.service.CustomDatabaseUserDetailsService;
import com.formation.service.DuplicataService;
import com.formation.service.JwtServiceAsym;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
public class DuplicataControlleur {

	// si des actions métiers sont à faire, appeler les beans de la couche métier
	@Autowired
	private final DuplicataService duplicataService;
	
	@Autowired
	private JwtServiceAsym jwtServiceAsym;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CustomDatabaseUserDetailsService userDetailsService;

	public DuplicataControlleur(DuplicataService duplicataService) {
		this.duplicataService = duplicataService;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/listDuplicatas")
	// @RequestMapping(value = "/duplicatas", method = RequestMethod.GET)
	public List<Duplicata> duplicatas() {
		return duplicataService.listDuplicatas();
	}
	
	@PostMapping("/duplicatas")
	public Duplicata createDuplicata(@RequestParam("user_id")  @NotBlank String userId, @Min(300) @Max(10000) @RequestParam Integer montant) {
		return duplicataService.createDuplicata(userId, montant);
	}
	
	 @PostMapping("/duplicatas/{userId}/{montant}")
	  public Duplicata createDuplicata_path(@PathVariable @NotBlank String userId, @PathVariable @Min(300) @Max(10000) Integer montant) {
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
		
		@GetMapping("/duplicatasSorted")
		public List<Duplicata> duplicatasSorted() {
			return duplicataService.findAllBySorting();
		}
		
		@GetMapping("/debug")
		public void debugUser() {
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    System.out.println("Username: " + auth.getName());
		    System.out.println("Authorities: " + auth.getAuthorities());
		}
		

		@PostMapping("/token")
		public String authentificationEtGenerationToken(@RequestBody RequeteLogin authRequest) {
		    Authentication authentication = authenticationManager.authenticate(
		        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

		    if (authentication.isAuthenticated()) {
		        // Récupérer les rôles du user
		        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		        List<String> roles = userDetails.getAuthorities().stream()
		                .map(grantedAuthority -> grantedAuthority.getAuthority())
		                .collect(Collectors.toList());

		        return jwtServiceAsym.generateToken(authRequest.getUsername(), roles);
		    } else {
		        throw new UsernameNotFoundException("Requête invalide !");
		    }
		}

	
}