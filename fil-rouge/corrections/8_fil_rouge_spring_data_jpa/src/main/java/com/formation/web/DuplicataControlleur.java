package com.formation.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.repository.projection.DuplicataResumeProjection;
import com.formation.service.DuplicataService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
public class DuplicataControlleur {

    private final DuplicataService duplicataService;

    public DuplicataControlleur(DuplicataService duplicataService) {
        this.duplicataService = duplicataService;
    }

    @GetMapping("/duplicatas")
    public List<Duplicata> duplicatas() {
        return duplicataService.getDuplicatas();
    }

    @PostMapping("/duplicatas")
    public Duplicata createDuplicata(@RequestParam("user_id") @NotBlank String userId,
            @RequestParam @Min(1000) @Max(7000) Integer montant) {
        return duplicataService.createDuplicata(userId, montant);
    }

    @PostMapping("/duplicatas/{userId}/{montant}")
    public Duplicata createDuplicata_path(@PathVariable @NotBlank String userId,
            @PathVariable @Min(1000) @Max(7000) Integer montant) {
        return duplicataService.createDuplicata(userId, montant);
    }

    @PostMapping("/duplicatas_dto")
    public Duplicata createDuplicata_dto(@RequestBody @Valid DuplicataDto duplicataDto) {
        return duplicataService.createDuplicata(duplicataDto.getUserId(), duplicataDto.getMontant());
    }

    // ---------------------------------------------------------------------
    // Endpoints ajoutés pour illustrer Spring Data JPA
    // ---------------------------------------------------------------------

    @GetMapping("/duplicatas/by-user/{userId}")
    public List<Duplicata> rechercherParUserId(@PathVariable String userId) {
    	return duplicataService.rechercherParUserId(userId);
    }

    @GetMapping("/duplicatas/by-montant")
    public List<Duplicata> rechercherParMontant(@RequestParam(defaultValue = "1000") int min,
            @RequestParam(defaultValue = "7000") int max) {
        
    	return duplicataService.rechercherParMontantEntre(min, max);
    }

    @GetMapping("/duplicatas/search")
    public List<Duplicata> rechercherParUserIdContenant(@RequestParam(defaultValue = "") String q) {
        return duplicataService.rechercherParUserIdContenant(q);
    }

    @GetMapping("/duplicatas/jpql")
    public List<Duplicata> rechercherAvecJpql(@RequestParam(defaultValue = "3000") int min) {
        return duplicataService.rechercherAvecJpql(min);
    }

    @GetMapping("/duplicatas/projections")
    public List<DuplicataResumeProjection> listerProjections(@RequestParam(defaultValue = "1000") int min) {
        return duplicataService.listerResumes(min);
    }

    @GetMapping("/duplicatas/page")
    public Page<Duplicata> rechercherPagee(@RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
    			return duplicataService.rechercherPagee(q, page, size, sort, direction);
    }
}
