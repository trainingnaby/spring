package com.formation.service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.exception.DuplicataException;
import com.formation.repository.DuplicataRepository;

@Service 
public class DuplicataService {

	@Autowired
	DuplicataRepository duplicataRepository;
	
	public List<Duplicata> listDuplicatas() {
		return duplicataRepository.findAll();
	}
	
	public void supprimerDuplicata(Duplicata duplicata) {
		duplicataRepository.delete(duplicata);
	}
	
	public Duplicata getDuplicata(String id) throws DuplicataException {
		Optional<Duplicata> byId = duplicataRepository.findById(id);
		if(byId.isPresent()) {
			return byId.get();
		}
		throw new DuplicataException("Duplicata not with id "+id+" not found !");
	}
	
	public Duplicata createDuplicata(String numeroFiscal, int montant, int annee) {
		
		Duplicata createdDuplicata = new Duplicata();
		String random_duplicata_id = UUID.randomUUID().toString();
		
		createdDuplicata.setId(random_duplicata_id);
		createdDuplicata.setAnnee(Year.of(annee));
		createdDuplicata.setMontant(montant);
		createdDuplicata.setNumeroFiscal(numeroFiscal);
		createdDuplicata.setPdfUrl("https://pdf.com");
		Duplicata saveDuplicata = duplicataRepository.save(createdDuplicata);
		
		return saveDuplicata;
		
	}
	
	public Page<Duplicata> listDuplicatasPageable(Pageable pageable) { 
		return duplicataRepository.findAll(pageable);
	}
	
	public List<Duplicata> listDuplicatasSorted() {  
		return duplicataRepository.findAll(Sort.by("montant"));
	}
	
	public boolean existsGreaterThan (int montant) {  
		return duplicataRepository.existsByMontantGreaterThan(montant);
	}

}
