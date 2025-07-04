package com.formation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.exception.DuplicataException;
import com.formation.exception.UserException;
import com.formation.repository.AppUserRepository;
import com.formation.repository.DuplicataRepository;

import jakarta.transaction.Transactional;

@Service 
public class DuplicataService {

	@Autowired
	DuplicataRepository duplicataRepository;
	
	
	@Autowired
	AppUserRepository appUserRepository;
	
	public List<Duplicata> listDuplicatas() {
		return duplicataRepository.findAll();
	}
	
	public void supprimerDuplicata(Duplicata duplicata) {
		duplicataRepository.delete(duplicata);
	}
	
	public Duplicata getDuplicata(long id) throws DuplicataException {
		Optional<Duplicata> byId = duplicataRepository.findById(id);
		if(byId.isPresent()) {
			return byId.get();
		}
		throw new DuplicataException("Duplicata not with id "+id+" not found !");
	}
	
	@Transactional
	public Duplicata createDuplicata(String numeroFiscal, int montant, int annee) throws UserException {
		
		if(!appUserRepository.existsByNumeroFiscal(numeroFiscal)) {
			throw new UserException("Le numero fiscal "+numeroFiscal+ " n'existe pas ! ");
		}
		
		Duplicata createdDuplicata = new Duplicata();
		
		createdDuplicata.setAnnee(annee);
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
